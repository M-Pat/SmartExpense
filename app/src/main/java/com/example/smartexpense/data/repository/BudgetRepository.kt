package com.example.smartexpense.data.repository

import android.content.Context

class BudgetRepository(context: Context) {
    private val prefs = context
        .getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DAILY  = "daily_budget"
        private const val KEY_WEEKLY = "weekly_budget"
    }

    fun getDailyBudget(): Double =
        prefs.getString(KEY_DAILY, "0.0")!!.toDoubleOrNull() ?: 0.0

    fun getWeeklyBudget(): Double =
        prefs.getString(KEY_WEEKLY, "0.0")!!.toDoubleOrNull() ?: 0.0

    fun setDailyBudget(amount: Double) {
        prefs.edit()
            .putString(KEY_DAILY, amount.toString())
            .apply()
    }

    fun setWeeklyBudget(amount: Double) {
        prefs.edit()
            .putString(KEY_WEEKLY, amount.toString())
            .apply()
    }
}
