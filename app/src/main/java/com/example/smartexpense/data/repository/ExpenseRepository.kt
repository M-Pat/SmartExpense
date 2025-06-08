
package com.example.smartexpense.data.repository

import android.content.Context
import com.example.smartexpense.data.local.db.ExpenseDatabaseHelper
import com.example.smartexpense.data.model.Expense

class ExpenseRepository(context: Context) {
    private val dbHelper = ExpenseDatabaseHelper(context)

    suspend fun addExpense(expense: Expense): Long =
        dbHelper.insertExpense(expense)

    suspend fun getAllExpenses(): List<Expense> =
        dbHelper.getAllExpenses()

    suspend fun deleteExpense(id: Long): Int =
        dbHelper.deleteExpense(id)

}
