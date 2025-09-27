package com.nebulae.impensa.presentation.home

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.compose.ui.graphics.Color
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tehras.charts.piechart.PieChartData
import com.nebulae.impensa.core.data.repository.ExpenseRepository
import com.nebulae.impensa.core.model.Expense
import com.nebulae.impensa.core.util.COLORMAP
import com.nebulae.impensa.core.util.PreferencesManager
import com.nebulae.impensa.core.util.extraColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.time.LocalDate

open class HomeViewModel(
    val repository: ExpenseRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

    private val _remarks: MutableStateFlow<String?> = MutableStateFlow("")
    val remarks: StateFlow<String?> = _remarks

    private val _customCategories = MutableStateFlow(COLORMAP)
    val customCategories: StateFlow<Map<String, Color>> = _customCategories
    private val _selectedCategory = MutableStateFlow("Select Category")
    val selectedCategory: StateFlow<String> = _selectedCategory
    val recentExpenses: StateFlow<List<Expense>> =
        repository.allExpenses.stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            emptyList()
        )
    private val _cachedExpenses = MutableStateFlow<List<Expense>>(emptyList())

    private val _editingExpense = MutableStateFlow<Expense?>(null)
    val editingExpense: StateFlow<Expense?> = _editingExpense
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme = _isDarkTheme

    private val _region = MutableStateFlow("IN")
    val region: MutableStateFlow<String> = _region

    private val _statsScreenState = MutableStateFlow(value = 0)
    val stateScreenState: MutableStateFlow<Int> = _statsScreenState

    private val _checkerState = MutableStateFlow(value = 0)
    val checkerState: MutableStateFlow<Int> = _checkerState

    init {
        viewModelScope.launch {
            preferencesManager.initializeDefaultCategoriesIfNeeded(COLORMAP)
        }

        viewModelScope.launch {
            recentExpenses.collect {
                _cachedExpenses.value = it
            }
        }

        viewModelScope.launch {
            preferencesManager.darkThemeFlow.collect { isDark ->
                _isDarkTheme.value = isDark
            }
        }

        viewModelScope.launch {
            preferencesManager.savedStatsScreenStateFlow.collect { statsScreenState ->
                _statsScreenState.value = statsScreenState
            }
        }

        viewModelScope.launch {
            preferencesManager.savedCategoryFlow.collect { categoryMap ->
                _customCategories.value = categoryMap
            }
        }
    }

    private val _latestVersion = MutableStateFlow<String?>(null)
    val latestVersion = _latestVersion.asStateFlow()
    private val client = OkHttpClient()

    fun checkForUpdates(context: Context) {
        _checkerState.value = 0
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(
                context.packageName,
                0
            )
        }

        val currentVersion = packageInfo.versionName ?: "Unknown"

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("https://raw.githubusercontent.com/Govind-Sankar/Impensa/main/version/version.json")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        val bodyString = response.body()?.string()
                        val json = JSONObject(bodyString ?: "{}")
                        val latestVersionStr = json.optString("version", "Unknown")
                        _latestVersion.value = latestVersionStr
                        delay(3000)
                        if (isNewerVersion(latestVersionStr, currentVersion)) {
                            _checkerState.value = 2 // Update available
                        } else {
                            _checkerState.value = 1 // Up-to-date
                        }
                    } else {
                        _latestVersion.value = "Error: ${response.code()}"
                        _checkerState.value = -1
                    }
                }
            } catch (e: Exception) {
                _latestVersion.value = "Error: ${e.message}"
                _checkerState.value = -1
            }
        }
    }

    private fun isNewerVersion(latest: String, current: String): Boolean {
        val latestParts = latest.split(".")
        val currentParts = current.split(".")
        val maxLength = maxOf(latestParts.size, currentParts.size)

        for (i in 0 until maxLength) {
            val latestPart = latestParts.getOrNull(i)?.toIntOrNull() ?: 0
            val currentPart = currentParts.getOrNull(i)?.toIntOrNull() ?: 0
            if (latestPart > currentPart) return true
            if (latestPart < currentPart) return false
        }
        return false
    }

    fun downloadAndInstallApk(context: Context) {
        val apkUrl = "https://github.com/Govind-Sankar/Impensa/releases/latest/download/Impensa.apk"
        val request = DownloadManager.Request(apkUrl.toUri())
            .setTitle("Downloading update")
            .setDescription("Please wait…")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverMetered(true)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "impensa-latest.apk")

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = dm.enqueue(request)

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context?, intent: Intent?) {
                val query = DownloadManager.Query().setFilterById(downloadId)
                dm.query(query).use { cursor ->
                    if (cursor.moveToFirst()) {
                        val uriString = cursor.getString(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI))
                        installApk(context, uriString.toUri())
                    }
                }
                context.unregisterReceiver(this)
            }
        }

        context.registerReceiver(
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            Context.RECEIVER_NOT_EXPORTED
        )
    }

    private fun installApk(context: Context, apkUri: Uri) {
        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(installIntent)
    }

    fun addCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank() || _customCategories.value.containsKey(trimmed)) return

        val nextColor = extraColors[_customCategories.value.size % extraColors.size]
        val updatedMap = _customCategories.value + (trimmed to nextColor)
        _customCategories.value = updatedMap

        viewModelScope.launch {
            preferencesManager.saveCategoryMap(updatedMap)
        }

    }

    fun deleteCategory(name: String) {
        val trimmed = name.trim()
        if (trimmed.isBlank() || !_customCategories.value.containsKey(trimmed)) return
        val updatedMap = _customCategories.value - trimmed
        _customCategories.value = updatedMap
        viewModelScope.launch {
            preferencesManager.saveCategoryMap(updatedMap)
            recentExpenses.value
                .filter { it.category == trimmed }
                .forEach { expense ->
                    repository.delete(expense)
                }
        }
    }

    fun setTheme(dark: Boolean) {
        _isDarkTheme.value = dark
        viewModelScope.launch {
            preferencesManager.setDarkTheme(dark)
        }
    }

    fun setStatsScreenState(state: Int) {
        _statsScreenState.value = state
        viewModelScope.launch {
            preferencesManager.setStatsScreenState(state)
        }
    }

    fun updateAmount(newAmount: String) {
        _amount.value = newAmount
    }

    fun updateSelectedCategory(newCategory: String) {
        _selectedCategory.value = newCategory
    }

    fun updateRemarks(newRemarks: String?) {
        _remarks.value = newRemarks
    }

//    fun updateSelectedPeriod(newPeriod: String) {
//        _selectedPeriod.value = newPeriod
//    }

    fun addExpense(amount: Double, category: String, date: String, remarks: String?) {
        val expense = Expense(amount = amount, category = category, date = date, remarks = remarks)
        viewModelScope.launch {
            repository.insert(expense)
            _amount.value = ""
            _selectedCategory.value = "Select Category"
            _remarks.value = null
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.delete(expense)
        }
    }

    fun getPieRatio(flag: Int = 0): Pair<List<PieChartData.Slice> , Pair<Map<String, Color>, Map<String,Double>>> {
        //val categoryToAmount = getMonthlyExpensesByCategory()
        val categoryToAmount = getTotalExpensesByCategory(flag)

        val slice = mutableListOf<PieChartData.Slice>()
        val colorMap = customCategories.value

        categoryToAmount.entries.sortedByDescending { it.value }.forEach { entry ->
            slice.add(
                PieChartData.Slice(
                    value = entry.value.toFloat(),
                    color = colorMap[entry.key]?: Color.Companion.Black
                )
            )
        }
        return Pair(slice, Pair(colorMap, categoryToAmount))
    }

    fun setEditingExpense(expense: Expense?) {
        _editingExpense.value = expense
        _amount.value = expense?.amount?.toString() ?: ""
        _selectedCategory.value = expense?.category ?: "Select Category"
    }

    fun updateExpense(expense: Expense, newAmount: Double, newCategory: String, newDate: String, newRemarks: String?) {
        val updatedExpense = expense.copy(
            amount = newAmount,
            category = newCategory,
            date = newDate,
            remarks = newRemarks
        )
        viewModelScope.launch {
            repository.update(updatedExpense)
            _editingExpense.value = null
            _amount.value = ""
            _selectedCategory.value = "Select Category"
            _remarks.value = null
        }
    }

    fun getTotalMonthlyExpense(): Double {
        val expenses = recentExpenses.value
        val month = LocalDate.now().monthValue
        var total = 0.0
        expenses.forEach {
            if (it.date.substring(5,7).toInt() == month && it.date.substring(0,4).toInt() == LocalDate.now().year){
                total += it.amount
            }
        }
        return total
    }

    fun getTotalExpense( flag: Int = 0 ): Double {
        val expenses = recentExpenses.value
        val month = LocalDate.now().monthValue
        var total = 0.0
        expenses.forEach {
            if (flag == 0) {
                if (it.date.substring(5, 7).toInt() == month && it.date.substring(0, 4)
                        .toInt() == LocalDate.now().year
                ) {
                    total += it.amount
                }
            } else if (flag == 1) {
                if (it.date.substring(0, 4).toInt() == LocalDate.now().year) {
                    total += it.amount
                }
            } else {
                total += it.amount
            }
        }
        return total
    }

    fun getMonthlyExpensesByCategory(): Map<String, Double> {
        val expenses = recentExpenses.value
        val monthlyExpenses = mutableMapOf<String, Double>()
        val month = LocalDate.now().monthValue
        expenses.forEach {
            if (it.date.substring(5,7).toInt() == month && it.date.substring(0,4).toInt() == LocalDate.now().year){
                val category = it.category
                val amount = it.amount
                monthlyExpenses[category] = monthlyExpenses.getOrDefault(category, 0.0) + amount
            }
        }
        return monthlyExpenses
    }

    fun getTotalExpensesByCategory( flag: Int = 0 ): Map<String, Double> {
        val expenses = recentExpenses.value
        val totalExpenses = mutableMapOf<String, Double>()
        val month = LocalDate.now().monthValue
        expenses.forEach {
            if (flag == 0) {
                if (it.date.substring(5, 7).toInt() == month && it.date.substring(0, 4)
                        .toInt() == LocalDate.now().year
                ) {
                    val category = it.category
                    val amount = it.amount
                    totalExpenses[category] = totalExpenses.getOrDefault(category, 0.0) + amount
                }
            } else if (flag == 1) {
                if (it.date.substring(0, 4).toInt() == LocalDate.now().year) {
                    val category = it.category
                    val amount = it.amount
                    totalExpenses[category] = totalExpenses.getOrDefault(category, 0.0) + amount
                }
            } else {
                val category = it.category
                val amount = it.amount
                totalExpenses[category] = totalExpenses.getOrDefault(category, 0.0) + amount
            }
        }
        return totalExpenses
    }

}

//    fun getBarRatio(): List<BarChartData.Bar> {
//        val expenses = recentExpenses.value
//        val categoryToAmount = getMonthlyExpensesByCategory()
//        val month = LocalDate.now().monthValue
//
//        val bar = mutableListOf<BarChartData.Bar>()
//
//        categoryToAmount.entries.sortedByDescending { it.value }.forEach { entry ->
//            bar.add(
//                BarChartData.Bar(
//                    label = entry.key,
//                    value = entry.value.toFloat(),
//                    color = COLORMAP[entry.key] ?: Color.Companion.Black
//                )
//            )
//        }
//
//        return bar
//    }