package com.nebulae.impensa.presentation.home

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.tehras.charts.bar.BarChartData
import com.github.tehras.charts.piechart.PieChartData
import com.nebulae.impensa.core.data.repository.ExpenseRepository
import com.nebulae.impensa.core.model.Expense
import com.nebulae.impensa.core.util.COLORMAP
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

open class HomeViewModel(val repository: ExpenseRepository) : ViewModel() {
    private val _amount = MutableStateFlow("")
    val amount: StateFlow<String> = _amount
    private val _selectedCategory = MutableStateFlow("Select Category")
    val selectedCategory: StateFlow<String> = _selectedCategory
    val recentExpenses: StateFlow<List<Expense>> =
        repository.allExpenses.stateIn(
            viewModelScope,
            SharingStarted.Companion.WhileSubscribed(5000),
            emptyList()
        )
    private val _cachedExpenses = MutableStateFlow<List<Expense>>(emptyList())
    init {
        viewModelScope.launch {
            recentExpenses.collect {
                _cachedExpenses.value = it
            }
        }
    }
    private val _editingExpense = MutableStateFlow<Expense?>(null)
    val editingExpense: StateFlow<Expense?> = _editingExpense

    fun updateAmount(newAmount: String) {
        _amount.value = newAmount
    }

    fun updateSelectedCategory(newCategory: String) {
        _selectedCategory.value = newCategory
    }

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

    fun getPieRatio(): Pair<List<PieChartData.Slice> , Pair<Map<String, Color>, Map<String,Double>>> {
        val expenses = recentExpenses.value
        val categoryToAmount = getMonthlyExpenses()

        val slice = mutableListOf<PieChartData.Slice>()
        val colorMap = COLORMAP

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

    fun getBarRatio(): List<BarChartData.Bar> {
        val expenses = recentExpenses.value
        val categoryToAmount = getMonthlyExpenses()
        val month = LocalDate.now().monthValue

        val bar = mutableListOf<BarChartData.Bar>()

        categoryToAmount.entries.sortedByDescending { it.value }.forEach { entry ->
            bar.add(
                BarChartData.Bar(
                    label = entry.key,
                    value = entry.value.toFloat(),
                    color = COLORMAP[entry.key] ?: Color.Companion.Black
                )
            )
        }

        return bar
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

    fun getMonthlyExpenses(): Map<String, Double> {
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

}