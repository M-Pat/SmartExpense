@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.example.smartexpense.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartexpense.viewmodel.BudgetViewModel
import com.example.smartexpense.viewmodel.ExpenseListViewModel
import com.example.smartexpense.viewmodel.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

// one day in ms
private const val DAY_MS = 24 * 60 * 60 * 1000L

@Composable
fun SummaryScreen(
    periodStart: Long,
    periodEnd:   Long,
    isWeek:      Boolean
) {
    val ctx = LocalContext.current.applicationContext as Context

    // VMs
    val expVm: ExpenseListViewModel = viewModel(factory = ViewModelFactory(ctx))
    val budVm: BudgetViewModel     = viewModel(factory = ViewModelFactory(ctx))

    val allExpenses   by expVm.items.collectAsStateWithLifecycle()
    val dailyBudget   by budVm.dailyBudget.collectAsState()
    val weeklyBudget  by budVm.weeklyBudget.collectAsState()

    // filter & totals
    val thisPeriod = remember(allExpenses, periodStart, periodEnd) {
        allExpenses.filter { it.timestamp in periodStart..periodEnd }
    }
    val total = remember(thisPeriod) { thisPeriod.sumOf { it.amount } }

    // prior period for delta
    val prevStart = periodStart - (periodEnd - periodStart + 1)
    val prevEnd   = periodStart - 1
    val prevTotal = remember(allExpenses, prevStart, prevEnd) {
        allExpenses.filter { it.timestamp in prevStart..prevEnd }
            .sumOf { it.amount }
    }
    val delta    = total - prevTotal

    // intervals & labels
    val intervals = remember(periodStart, periodEnd, isWeek) {
        if (isWeek) {
            (0 until 7).map { i ->
                val s = periodStart + i * DAY_MS
                val e = (s + DAY_MS - 1).coerceAtMost(periodEnd)
                s to e
            }
        } else {
            buildList {
                var cur = periodStart
                while (cur <= periodEnd) {
                    val e = (cur + 7*DAY_MS - 1).coerceAtMost(periodEnd)
                    add(cur to e)
                    cur = e + 1
                }
            }
        }
    }
    val labels = remember(intervals, isWeek) {
        if (isWeek) listOf("Mon","Tue","Wed","Thu","Fri","Sat","Sun")
        else {
            val fmt = SimpleDateFormat("dd", Locale.getDefault())
            intervals.map { (s, e) -> "${fmt.format(Date(s))}-${fmt.format(Date(e))}" }
        }
    }

    // amounts and max
    val amounts = remember(thisPeriod, intervals) {
        intervals.map { (s,e) ->
            thisPeriod.filter { it.timestamp in s..e }
                .sumOf { it.amount }
        }
    }
    val maxAmt = (amounts.maxOrNull() ?: 1.0).coerceAtLeast(1.0)

    Scaffold(topBar = {
        TopAppBar(title = { Text("Summary") })
    }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Totals card
            Card(
                Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Total this period", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "\$${"%.2f".format(total)}",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Divider()
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Change vs prior", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "${if (delta >= 0) "+" else ""}${"%.2f".format(delta)}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = if (delta >= 0)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }

            // Histogram with budget line
            Column {
                Text(
                    if (isWeek) "Daily Breakdown" else "Weekly Breakdown",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                Box(Modifier.fillMaxWidth().height(220.dp)) {
                    // budget line
                    val budgetValue = if (isWeek) dailyBudget else weeklyBudget
                    val frac         = (budgetValue / maxAmt).toFloat().coerceIn(0f,1f)
                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomStart)
                            .offset(y = - (220.dp * frac))
                            .height(2.dp),
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // bars
                    Row(
                        Modifier
                            .fillMaxSize()
                            .padding(bottom = 16.dp),
                        verticalAlignment   = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        amounts.forEach { amt ->
                            val heightFrac = (amt / maxAmt).toFloat().coerceIn(0f,1f)
                            Box(
                                Modifier
                                    .weight(1f)
                                    .fillMaxHeight(fraction = heightFrac)
                                    .background(
                                        color = if (amt > budgetValue)
                                            MaterialTheme.colorScheme.error
                                        else
                                            MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                            )
                        }
                    }

                    // labels
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        labels.forEach { Text(it, style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }
        }
    }
}
