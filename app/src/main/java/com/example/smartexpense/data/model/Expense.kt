package com.example.smartexpense.data.model

data class Expense(
    val amount: Double,
    val description: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)