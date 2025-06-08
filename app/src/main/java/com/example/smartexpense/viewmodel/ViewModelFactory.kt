package com.example.smartexpense.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartexpense.data.repository.ExpenseRepository

class ViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    private val repo = ExpenseRepository(context)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AddExpenseViewModel::class.java) ->
            AddExpenseViewModel(repo) as T

        modelClass.isAssignableFrom(ExpenseListViewModel::class.java) ->
            ExpenseListViewModel(repo) as T

        modelClass.isAssignableFrom(BudgetViewModel::class.java) ->
            BudgetViewModel(context) as T

        else -> throw IllegalArgumentException("Unknown ViewModel: $modelClass")
    }
}
