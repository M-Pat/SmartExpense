@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smartexpense.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartexpense.viewmodel.AddExpenseViewModel
import java.text.SimpleDateFormat
import java.util.*

private data class Category(val name: String, val icon: ImageVector)
private val categories = listOf(
    Category("Food", Icons.Filled.FoodBank),
    Category("Travel", Icons.Filled.FlightTakeoff),
    Category("Bills", Icons.Filled.Receipt),
    Category("Entertainment", Icons.Filled.Movie),
    Category("Investment", Icons.Filled.TrendingUp),
    Category("Misc", Icons.Filled.MoreHoriz),
)

@Composable
fun AddExpenseScreen(
    viewModel: AddExpenseViewModel,
    onSaved:    () -> Unit
) {
    val saved by viewModel.saved.collectAsStateWithLifecycle()
    LaunchedEffect(saved) {
        if (saved) onSaved()
    }

    val context = LocalContext.current

    // 1) amount, desc, category states
    var amount by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(categories.first().name) }

    // 2) date state (epoch millis) + a DatePickerDialog
    var timestamp by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            timestamp = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Add Expense") }) }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category slider
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { cat ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { selectedCategory = cat.name }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = cat.icon,
                            contentDescription = cat.name,
                            tint = if (cat.name == selectedCategory)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            cat.name,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (cat.name == selectedCategory)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Amount field
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth()
            )

            // Description field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            // Date picker field
            OutlinedTextField(
                value = dateFormatter.format(Date(timestamp)),
                onValueChange = { /* read-only */ },
                label = { Text("Date") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePicker.show() }
            )

            // Save button passes the timestamp
            Button(
                onClick = {
                    viewModel.save(
                        amountStr   = amount,
                        description = description,
                        category    = selectedCategory,
                        timestamp   = timestamp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Save")
            }
        }
    }
}
