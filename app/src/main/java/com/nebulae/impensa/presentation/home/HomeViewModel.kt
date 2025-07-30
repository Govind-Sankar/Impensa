package com.nebulae.impensa.presentation.home

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tehras.charts.piechart.PieChartData
import com.nebulae.impensa.core.data.repository.ExpenseRepository
import com.nebulae.impensa.core.model.Expense
import com.nebulae.impensa.core.util.COLORMAP
import com.nebulae.impensa.core.util.PreferencesManager
import com.nebulae.impensa.core.util.extraColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.exp

open class HomeViewModel(
    val repository: ExpenseRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount

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

//    private val _selectedPeriod = MutableStateFlow(LocalDate.now().month.toString() + " " + LocalDate.now().year.toString())
//    val selectedPeriod: StateFlow<String> = _selectedPeriod

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
            preferencesManager.savedCategoryFlow.collect { categoryMap ->
                _customCategories.value = categoryMap
            }
        }
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

    fun updateAmount(newAmount: String) {
        _amount.value = newAmount
    }

    fun updateSelectedCategory(newCategory: String) {
        _selectedCategory.value = newCategory
    }

//    fun updateSelectedPeriod(newPeriod: String) {
//        _selectedPeriod.value = newPeriod
//    }

    fun addExpense(amount: Double, category: String, date: String) {
        val expense = Expense(amount = amount, category = category, date = date)
        viewModelScope.launch {
            repository.insert(expense)
            Log.d("ExpenseDebug", "Expense added: $expense")
            _amount.value = ""
            _selectedCategory.value = "Select Category"
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

    fun updateExpense(expense: Expense, newAmount: Double, newCategory: String, newDate: String) {
        val updatedExpense = expense.copy(
            amount = newAmount,
            category = newCategory,
            date = newDate
        )
        viewModelScope.launch {
            repository.update(updatedExpense)
            _editingExpense.value = null
            _amount.value = ""
            _selectedCategory.value = "Select Category"
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