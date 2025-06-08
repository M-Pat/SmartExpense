package com.example.smartexpense.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpense.data.model.Expense
import com.example.smartexpense.data.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExpenseListViewModel(
    private val repo: ExpenseRepository
) : ViewModel() {

    private val _items = MutableStateFlow<List<Expense>>(emptyList())
    val items: StateFlow<List<Expense>> = _items.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _items.value = repo.getAllExpenses()
        }
    }

    fun loadExpenses() {
        viewModelScope.launch(Dispatchers.IO) {
            _items.value = repo.getAllExpenses()
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteExpense(expense.id)
            _items.value = repo.getAllExpenses()
        }
    }
}
