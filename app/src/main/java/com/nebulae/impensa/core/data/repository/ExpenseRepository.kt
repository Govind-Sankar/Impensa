package com.nebulae.impensa.core.data.repository

import com.nebulae.impensa.core.data.local.ExpenseDao
import com.nebulae.impensa.core.model.Expense
import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {

    val allExpenses: Flow<List<Expense>> = dao.getAllExpenses()

    suspend fun insert(expense: Expense) {
        dao.insertExpense(expense)
    }

    suspend fun delete(expense: Expense) {
        dao.deleteExpense(expense)
    }

    suspend fun update(expense: Expense) {
        dao.update(expense)
    }
}