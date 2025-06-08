package com.example.smartexpense.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.smartexpense.data.local.preferences.PreferenceManager

class BudgetViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = PreferenceManager(app.applicationContext)

    private val _dailyBudget  = MutableStateFlow(prefs.dailyBudget)
    private val _weeklyBudget = MutableStateFlow(prefs.weeklyBudget)

    val dailyBudget  = _dailyBudget.asStateFlow()
    val weeklyBudget = _weeklyBudget.asStateFlow()

    fun setDaily(value: Double) {
        prefs.dailyBudget = value
        _dailyBudget.value = value
    }

    fun setWeekly(value: Double) {
        prefs.weeklyBudget = value
        _weeklyBudget.value = value
    }
}
