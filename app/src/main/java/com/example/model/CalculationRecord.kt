package com.example.model

data class CalculationRecord(
    val id: Long = System.currentTimeMillis(),
    val expression: String,
    val result: String,
    val timestamp: String = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
)
