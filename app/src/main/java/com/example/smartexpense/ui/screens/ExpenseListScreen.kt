@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.smartexpense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartexpense.data.model.Expense
import com.example.smartexpense.viewmodel.ExpenseListViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExpenseListScreen(
    viewModel: ExpenseListViewModel,
    onAddExpense:    () -> Unit,
    onAddBudget:     () -> Unit,
    onSettings:      () -> Unit,
    onWeekSummary:   (start: Long, end: Long) -> Unit,
    onMonthSummary:  (start: Long, end: Long) -> Unit
) {
    val allExpenses by viewModel.items.collectAsStateWithLifecycle(emptyList())

    val weekPeriods = remember(allExpenses) {
        allExpenses
            .map { it.timestamp.toWeekRange() }
            .distinct()
            .sortedByDescending { it.first }
    }
    val monthPeriods = remember(allExpenses) {
        allExpenses
            .map { it.timestamp.toMonthRange() }
            .distinct()
            .sortedByDescending { it.first }
    }

    var menuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (allExpenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    "No expenses yet.\nTap + to get started.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            var modeTab by rememberSaveable { mutableStateOf(0) }
            TabRow(selectedTabIndex = modeTab) {
                listOf("Weekly", "Monthly").forEachIndexed { i, title ->
                    Tab(
                        text     = { Text(title) },
                        selected = (modeTab == i),
                        onClick  = { modeTab = i }
                    )
                }
            }

            val periods = if (modeTab == 0) weekPeriods else monthPeriods
            var periodTab by rememberSaveable { mutableStateOf(0) }

            LaunchedEffect(periods) {
                if (periodTab >= periods.size) {
                    periodTab = periods.lastIndex.coerceAtLeast(0)
                }
            }
            ScrollableTabRow(selectedTabIndex = periodTab) {
                periods.forEachIndexed { idx, (start, end) ->
                    val label = if (modeTab == 0) {
                        SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(start)) +
                                " – " +
                                SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(end))
                    } else {
                        SimpleDateFormat("MMM yyyy", Locale.getDefault()).format(Date(start))
                    }
                    Tab(
                        text     = { Text(label) },
                        selected = (periodTab == idx),
                        onClick  = { periodTab = idx }
                    )
                }
            }

            val (selStart, selEnd) = periods.getOrNull(periodTab) ?: (0L to 0L)
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    if (modeTab == 0) onWeekSummary(selStart, selEnd)
                    else              onMonthSummary(selStart, selEnd)
                }) {
                    Text("View Summary")
                }
            }

            val filtered = allExpenses.filter { it.timestamp in selStart..selEnd }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filtered) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onDelete = { viewModel.deleteExpense(expense) }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            var menuExpanded by remember { mutableStateOf(false) }

            Box {
                FloatingActionButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    offset = DpOffset(x = (-60).dp, y = (-8).dp),
                ) {
                    DropdownMenuItem(
                        text = { Text("Add Expense") },
                        onClick = {
                            menuExpanded = false
                            onAddExpense()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Set Budget") },
                        onClick = {
                            menuExpanded = false
                            onAddBudget()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            menuExpanded = false
                            onSettings()
                        }
                    )
                }
            }
        }

    }
}

@Composable
private fun ExpenseCard(
    expense: Expense,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmDelete by remember { mutableStateOf(false) }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title   = { Text("Delete expense?") },
            text    = { Text("Are you sure you want to permanently delete this expense?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    onDelete()
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) {
                    Text("No")
                }
            }
        )
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    expense.category,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { confirmDelete = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete"
                        )
                    }
                    Text(
                        "\$${"%.2f".format(expense.amount)}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(expense.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(expense.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Helpers
private fun Long.toWeekRange(): Pair<Long, Long> {
    val cal = Calendar.getInstance().apply {
        timeInMillis = this@toWeekRange
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val start = cal.timeInMillis
    val end = start + 7 * 24 * 60 * 60 * 1000 - 1
    return start to end
}

private fun Long.toMonthRange(): Pair<Long, Long> {
    val cal = Calendar.getInstance().apply {
        timeInMillis = this@toMonthRange
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }
    val start = cal.timeInMillis
    cal.add(Calendar.MONTH, 1)
    val end = cal.timeInMillis - 1
    return start to end
}
