package com.example.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CalculatorViewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val expression by viewModel.expression.collectAsState()
    val result by viewModel.result.collectAsState()
    val isScientific by viewModel.isScientificMode.collectAsState()
    val isRadians by viewModel.isRadians.collectAsState()
    val memory by viewModel.memory.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Toolbar / Toggles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = isScientific,
                    onClick = { viewModel.toggleScientificMode() },
                    label = { Text(if (isScientific) "Scientific" else "Standard") },
                    leadingIcon = { Icon(Icons.Default.Science, contentDescription = null) }
                )
                if (isScientific) {
                    FilterChip(
                        selected = isRadians,
                        onClick = { viewModel.toggleRadians() },
                        label = { Text(if (isRadians) "RAD" else "DEG") }
                    )
                }
            }

            memory?.let { mem ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "M: ${if (mem == mem.toLong().toDouble()) mem.toLong() else mem}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display Screen
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.3f),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = expression.ifEmpty { "0" },
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 28.sp
                    ),
                    textAlign = TextAlign.End,
                    maxLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.ifEmpty { " " },
                    style = MaterialTheme.typography.displayLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 44.sp
                    ),
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Keypad Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.65f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isScientific) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalcButton("sin(", modifier = Modifier.weight(1f), isSpecial = true) { viewModel.onAction("sin(") }
                    CalcButton("cos(", modifier = Modifier.weight(1f), isSpecial = true) { viewModel.onAction("cos(") }
                    CalcButton("tan(", modifier = Modifier.weight(1f), isSpecial = true) { viewModel.onAction("tan(") }
                    CalcButton("√", modifier = Modifier.weight(1f), isSpecial = true) { viewModel.onAction("sqrt(") }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalcButton("π", modifier = Modifier.weight(1f), isSpecial = true) { viewModel.onAction("π") }
                    CalcButton("e", modifier = Modifier.weight(1f), isSpecial = true) { viewModel.onAction("e") }
                    CalcButton("^", modifier = Modifier.weight(1f), isSpecial = true) { viewModel.onAction("^") }
                    CalcButton("fact(", modifier = Modifier.weight(1f), isSpecial = true) { viewModel.onAction("fact(") }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CalcButton("MC", modifier = Modifier.weight(1f), isMemory = true) { viewModel.onAction("MC") }
                    CalcButton("MR", modifier = Modifier.weight(1f), isMemory = true) { viewModel.onAction("MR") }
                    CalcButton("M+", modifier = Modifier.weight(1f), isMemory = true) { viewModel.onAction("M+") }
                    CalcButton("M-", modifier = Modifier.weight(1f), isMemory = true) { viewModel.onAction("M-") }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton("AC", modifier = Modifier.weight(1f), isAction = true) { viewModel.onAction("AC") }
                CalcButton("(", modifier = Modifier.weight(1f), isAction = true) { viewModel.onAction("(") }
                CalcButton(")", modifier = Modifier.weight(1f), isAction = true) { viewModel.onAction(")") }
                CalcButton("÷", modifier = Modifier.weight(1f), isOperator = true) { viewModel.onAction("÷") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton("7", modifier = Modifier.weight(1f)) { viewModel.onAction("7") }
                CalcButton("8", modifier = Modifier.weight(1f)) { viewModel.onAction("8") }
                CalcButton("9", modifier = Modifier.weight(1f)) { viewModel.onAction("9") }
                CalcButton("×", modifier = Modifier.weight(1f), isOperator = true) { viewModel.onAction("×") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton("4", modifier = Modifier.weight(1f)) { viewModel.onAction("4") }
                CalcButton("5", modifier = Modifier.weight(1f)) { viewModel.onAction("5") }
                CalcButton("6", modifier = Modifier.weight(1f)) { viewModel.onAction("6") }
                CalcButton("-", modifier = Modifier.weight(1f), isOperator = true) { viewModel.onAction("-") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton("1", modifier = Modifier.weight(1f)) { viewModel.onAction("1") }
                CalcButton("2", modifier = Modifier.weight(1f)) { viewModel.onAction("2") }
                CalcButton("3", modifier = Modifier.weight(1f)) { viewModel.onAction("3") }
                CalcButton("+", modifier = Modifier.weight(1f), isOperator = true) { viewModel.onAction("+") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalcButton("0", modifier = Modifier.weight(1f)) { viewModel.onAction("0") }
                CalcButton(".", modifier = Modifier.weight(1f)) { viewModel.onAction(".") }
                CalcButton("⌫", modifier = Modifier.weight(1f), isAction = true) { viewModel.onAction("C") }
                CalcButton("=", modifier = Modifier.weight(1f), isEqual = true) { viewModel.onAction("=") }
            }
        }
    }
}

@Composable
fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    isOperator: Boolean = false,
    isAction: Boolean = false,
    isSpecial: Boolean = false,
    isMemory: Boolean = false,
    isEqual: Boolean = false,
    onClick: () -> Unit
) {
    val containerColor = when {
        isEqual -> MaterialTheme.colorScheme.primary
        isOperator -> MaterialTheme.colorScheme.tertiaryContainer
        isAction || isSpecial -> MaterialTheme.colorScheme.secondaryContainer
        isMemory -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }

    val contentColor = when {
        isEqual -> MaterialTheme.colorScheme.onPrimary
        isOperator -> MaterialTheme.colorScheme.onTertiaryContainer
        isAction || isSpecial -> MaterialTheme.colorScheme.onSecondaryContainer
        isMemory -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp)),
        color = containerColor,
        tonalElevation = if (isEqual || isOperator) 4.dp else 1.dp
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = contentColor,
                    fontSize = 20.sp
                )
            )
        }
    }
}
