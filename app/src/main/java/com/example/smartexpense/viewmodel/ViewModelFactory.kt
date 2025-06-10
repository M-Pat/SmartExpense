package com.example.smartexpense.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartexpense.data.repository.ExpenseRepository

class ViewModelFactory(
    private val app: Application
) : ViewModelProvider.Factory {

    private val repo = ExpenseRepository(app.applicationContext)

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AddExpenseViewModel::class.java) ->
            AddExpenseViewModel(repo) as T

        modelClass.isAssignableFrom(ExpenseListViewModel::class.java) ->
            ExpenseListViewModel(repo) as T

        modelClass.isAssignableFrom(BudgetViewModel::class.java) ->
            BudgetViewModel(app) as T

        else -> throw IllegalArgumentException("Unknown ViewModel: $modelClass")
    }
}
