@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.smartexpense.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartexpense.viewmodel.BudgetViewModel
import com.example.smartexpense.viewmodel.ViewModelFactory

@Composable
fun BudgetScreen(onBack: () -> Unit) {
    val app = LocalContext.current.applicationContext as Application
    val factory = ViewModelFactory(app)
    val vm: BudgetViewModel = viewModel(factory = factory)

    val daily by vm.dailyBudget.collectAsState()
    val weekly by vm.weeklyBudget.collectAsState()

    var dailyStr by remember { mutableStateOf(daily.toString()) }
    var weeklyStr by remember { mutableStateOf(weekly.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Set Budgets") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = dailyStr,
                onValueChange = { dailyStr = it },
                label = { Text("Daily Budget") },
                singleLine = true
            )
            OutlinedTextField(
                value = weeklyStr,
                onValueChange = { weeklyStr = it },
                label = { Text("Weekly Budget") },
                singleLine = true
            )
            Button(
                onClick = {
                    vm.setDaily(dailyStr.toDoubleOrNull() ?: 0.0)
                    vm.setWeekly(weeklyStr.toDoubleOrNull() ?: 0.0)
                    onBack()
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
