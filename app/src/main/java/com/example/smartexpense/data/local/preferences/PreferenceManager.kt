package com.example.smartexpense.data.local.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("smart_expense_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_DAILY_BUDGET  = "daily_budget"
        private const val KEY_WEEKLY_BUDGET = "weekly_budget"
        private const val KEY_CURRENCY      = "currency"
    }

    var dailyBudget: Double
        get() = prefs.getFloat(KEY_DAILY_BUDGET, 0f).toDouble()
        set(value) = prefs.edit().putFloat(KEY_DAILY_BUDGET, value.toFloat()).apply()

    var weeklyBudget: Double
        get() = prefs.getFloat(KEY_WEEKLY_BUDGET, 0f).toDouble()
        set(value) = prefs.edit().putFloat(KEY_WEEKLY_BUDGET, value.toFloat()).apply()

    var currency: String
        get() = prefs.getString(KEY_CURRENCY, "USD") ?: "USD"
        set(value) = prefs.edit().putString(KEY_CURRENCY, value).apply()
}