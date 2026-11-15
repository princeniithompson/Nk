package com.example.ui.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConverterScreen(modifier: Modifier = Modifier) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Unit Converter, 1: Tip Calculator

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Tools & Converters",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Unit Converter") }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Tip Calculator") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedTab == 0) {
            UnitConverterSection()
        } else {
            TipCalculatorSection()
        }
    }
}

@Composable
fun UnitConverterSection() {
    var category by remember { mutableStateOf("Length") }
    var inputValue by remember { mutableStateOf("1") }
    var fromUnit by remember { mutableStateOf("Meters") }
    var toUnit by remember { mutableStateOf("Feet") }

    val categories = listOf("Length", "Weight", "Temperature")
    val unitsMap = mapOf(
        "Length" to listOf("Meters", "Kilometers", "Centimeters", "Inches", "Feet", "Miles"),
        "Weight" to listOf("Kilograms", "Grams", "Pounds", "Ounces"),
        "Temperature" to listOf("Celsius", "Fahrenheit", "Kelvin")
    )

    val currentUnits = unitsMap[category] ?: emptyList()

    val result = remember(inputValue, category, fromUnit, toUnit) {
        val num = inputValue.toDoubleOrNull() ?: 0.0
        convertUnits(num, category, fromUnit, toUnit)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Select Category", style = MaterialTheme.typography.titleMedium)
            ScrollableTabRow(selectedTabIndex = categories.indexOf(category), edgePadding = 0.dp) {
                categories.forEach { cat ->
                    Tab(
                        selected = category == cat,
                        onClick = {
                            category = cat
                            val newUnits = unitsMap[cat] ?: emptyList()
                            fromUnit = newUnits.getOrElse(0) { "" }
                            toUnit = newUnits.getOrElse(1) { newUnits.first() }
                        },
                        text = { Text(cat) }
                    )
                }
            }

            OutlinedTextField(
                value = inputValue,
                onValueChange = { inputValue = it },
                label = { Text("Value to convert") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("From: $fromUnit", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                ScrollableTabRow(selectedTabIndex = currentUnits.indexOf(fromUnit), edgePadding = 0.dp) {
                    currentUnits.forEach { unit ->
                        Tab(
                            selected = fromUnit == unit,
                            onClick = { fromUnit = unit },
                            text = { Text(unit) }
                        )
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Text("To: $toUnit", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                ScrollableTabRow(selectedTabIndex = currentUnits.indexOf(toUnit), edgePadding = 0.dp) {
                    currentUnits.forEach { unit ->
                        Tab(
                            selected = toUnit == unit,
                            onClick = { toUnit = unit },
                            text = { Text(unit) }
                        )
                    }
                }
            }

            Divider()

            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Converted Result", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(java.util.Locale.US, "%.4f %s", result, toUnit),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

fun convertUnits(value: Double, category: String, from: String, to: String): Double {
    if (from == to) return value
    return when (category) {
        "Length" -> {
            val meters = when (from) {
                "Meters" -> value
                "Kilometers" -> value * 1000.0
                "Centimeters" -> value / 100.0
                "Inches" -> value * 0.0254
                "Feet" -> value * 0.3048
                "Miles" -> value * 1609.34
                else -> value
            }
            when (to) {
                "Meters" -> meters
                "Kilometers" -> meters / 1000.0
                "Centimeters" -> meters * 100.0
                "Inches" -> meters / 0.0254
                "Feet" -> meters / 0.3048
                "Miles" -> meters / 1609.34
                else -> meters
            }
        }
        "Weight" -> {
            val kg = when (from) {
                "Kilograms" -> value
                "Grams" -> value / 1000.0
                "Pounds" -> value * 0.453592
                "Ounces" -> value * 0.0283495
                else -> value
            }
            when (to) {
                "Kilograms" -> kg
                "Grams" -> kg * 1000.0
                "Pounds" -> kg / 0.453592
                "Ounces" -> kg / 0.0283495
                else -> kg
            }
        }
        "Temperature" -> {
            val celsius = when (from) {
                "Celsius" -> value
                "Fahrenheit" -> (value - 32) * 5 / 9
                "Kelvin" -> value - 273.15
                else -> value
            }
            when (to) {
                "Celsius" -> celsius
                "Fahrenheit" -> celsius * 9 / 5 + 32
                "Kelvin" -> celsius + 273.15
                else -> celsius
            }
        }
        else -> value
    }
}

@Composable
fun TipCalculatorSection() {
    var billAmount by remember { mutableStateOf("50.00") }
    var tipPercent by remember { mutableStateOf(15) }
    var splitCount by remember { mutableStateOf(1) }

    val bill = billAmount.toDoubleOrNull() ?: 0.0
    val tipAmount = bill * (tipPercent / 100.0)
    val totalAmount = bill + tipAmount
    val perPerson = if (splitCount > 0) totalAmount / splitCount else totalAmount

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(text = "Tip & Split Calculator", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = billAmount,
                onValueChange = { billAmount = it },
                label = { Text("Bill Amount ($)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(text = "Tip Percentage: $tipPercent%")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(10, 15, 18, 20, 25).forEach { pct ->
                    FilterChip(
                        selected = tipPercent == pct,
                        onClick = { tipPercent = pct },
                        label = { Text("$pct%") }
                    )
                }
            }

            Text(text = "Split between: $splitCount people")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(1, 2, 3, 4, 5).forEach { count ->
                    FilterChip(
                        selected = splitCount == count,
                        onClick = { splitCount = count },
                        label = { Text("$count") }
                    )
                }
            }

            Divider()

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tip Amount:")
                Text(String.format(java.util.Locale.US, "$%.2f", tipAmount), style = MaterialTheme.typography.titleMedium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Bill:")
                Text(String.format(java.util.Locale.US, "$%.2f", totalAmount), style = MaterialTheme.typography.titleMedium)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Per Person:", color = MaterialTheme.colorScheme.primary)
                Text(String.format(java.util.Locale.US, "$%.2f", perPerson), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
