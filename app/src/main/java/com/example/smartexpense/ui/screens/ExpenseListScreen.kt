@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smartexpense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartexpense.R
import com.example.smartexpense.data.model.Expense
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpenseListScreen(onAddClicked: () -> Unit) {
    val sampleExpenses = remember {
        mutableStateListOf(
            Expense(12.5, "Lunch at Cafe", "Food"),
            Expense(7.99, "Coffee & Snack", "Food", System.currentTimeMillis() - 86_400_000),
            Expense(23.0, "Taxi ride", "Transport", System.currentTimeMillis() - 2 * 86_400_000)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Expenses") },
                // use the universally-available API:
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClicked) {
                Icon(
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = "Add expense"
                )
            }
        }
    ) { innerPadding ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (sampleExpenses.isEmpty()) {
                Text(
                    "No expenses yet.\nTap + to get started.",
                    Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sampleExpenses) { expense ->
                        ExpenseCard(expense)
                    }
                }
            }
        }
    }
}


@Composable
fun ExpenseCard(expense: Expense, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    expense.category,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    String.format("$%.2f", expense.amount),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(expense.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(Date(expense.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
