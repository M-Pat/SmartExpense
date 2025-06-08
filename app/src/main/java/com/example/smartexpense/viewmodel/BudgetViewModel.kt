package com.example.smartexpense.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartexpense.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BudgetViewModel(context: Context) : ViewModel() {
    private val repo = BudgetRepository(context)

    private val _daily = MutableStateFlow(repo.getDailyBudget())
    val dailyBudget: StateFlow<Double> = _daily.asStateFlow()

    private val _weekly = MutableStateFlow(repo.getWeeklyBudget())
    val weeklyBudget: StateFlow<Double> = _weekly.asStateFlow()

    fun setDaily(amount: Double) = viewModelScope.launch {
        repo.setDailyBudget(amount)
        _daily.value = amount
    }

    fun setWeekly(amount: Double) = viewModelScope.launch {
        repo.setWeeklyBudget(amount)
        _weekly.value = amount
    }
}
