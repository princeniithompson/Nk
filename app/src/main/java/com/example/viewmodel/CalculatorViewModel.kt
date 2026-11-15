package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.model.CalculationRecord
import com.example.utils.MathEvaluator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class CalculatorViewModel : ViewModel() {

    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _result = MutableStateFlow("")
    val result: StateFlow<String> = _result.asStateFlow()

    private val _history = MutableStateFlow<List<CalculationRecord>>(emptyList())
    val history: StateFlow<List<CalculationRecord>> = _history.asStateFlow()

    private val _isScientificMode = MutableStateFlow(false)
    val isScientificMode: StateFlow<Boolean> = _isScientificMode.asStateFlow()

    private val _isRadians = MutableStateFlow(false)
    val isRadians: StateFlow<Boolean> = _isRadians.asStateFlow()

    private val _memory = MutableStateFlow<Double?>(null)
    val memory: StateFlow<Double?> = _memory.asStateFlow()

    private val _selectedTab = MutableStateFlow(0) // 0: Calculator, 1: History, 2: Converter/Tip
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun toggleScientificMode() {
        _isScientificMode.update { !it }
    }

    fun toggleRadians() {
        _isRadians.update { !it }
    }

    fun onAction(action: String) {
        when (action) {
            "AC" -> {
                _expression.value = ""
                _result.value = ""
            }
            "C" -> {
                val current = _expression.value
                if (current.isNotEmpty()) {
                    _expression.value = current.dropLast(1)
                    updateLiveResult(_expression.value)
                }
            }
            "=" -> {
                calculateResult(saveToHistory = true)
            }
            "MC" -> {
                _memory.value = null
            }
            "MR" -> {
                _memory.value?.let { mem ->
                    val formatted = if (mem == mem.toLong().toDouble()) mem.toLong().toString() else mem.toString()
                    _expression.value += formatted
                    updateLiveResult(_expression.value)
                }
            }
            "M+" -> {
                val res = _result.value.toDoubleOrNull() ?: _expression.value.toDoubleOrNull()
                res?.let { v ->
                    val currentMem = _memory.value ?: 0.0
                    _memory.value = currentMem + v
                }
            }
            "M-" -> {
                val res = _result.value.toDoubleOrNull() ?: _expression.value.toDoubleOrNull()
                res?.let { v ->
                    val currentMem = _memory.value ?: 0.0
                    _memory.value = currentMem - v
                }
            }
            else -> {
                _expression.value += action
                updateLiveResult(_expression.value)
            }
        }
    }

    private fun updateLiveResult(expr: String) {
        if (expr.isBlank()) {
            _result.value = ""
            return
        }
        MathEvaluator.evaluate(expr, _isRadians.value).onSuccess { res ->
            _result.value = formatResult(res)
        }.onFailure {
            _result.value = ""
        }
    }

    fun calculateResult(saveToHistory: Boolean = false) {
        val expr = _expression.value
        if (expr.isBlank()) return

        MathEvaluator.evaluate(expr, _isRadians.value).onSuccess { res ->
            val formattedRes = formatResult(res)
            _result.value = formattedRes
            if (saveToHistory) {
                val record = CalculationRecord(expression = expr, result = formattedRes)
                _history.update { listOf(record) + it }
                _expression.value = formattedRes
            }
        }.onFailure { err ->
            _result.value = "Error: ${err.localizedMessage ?: "Invalid"}"
        }
    }

    private fun formatResult(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            String.format(java.util.Locale.US, "%.8f", value).trimEnd('0').trimEnd('.')
        }
    }

    fun clearHistory() {
        _history.value = emptyList()
    }

    fun deleteHistoryItem(id: Long) {
        _history.update { list -> list.filter { it.id != id } }
    }

    fun loadHistoryItem(record: CalculationRecord) {
        _expression.value = record.expression
        _result.value = record.result
        _selectedTab.value = 0
    }
}
