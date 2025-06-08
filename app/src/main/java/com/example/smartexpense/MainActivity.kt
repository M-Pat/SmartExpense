package com.example.smartexpense

import android.os.Bundle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartexpense.ui.theme.SmartExpenseTheme
import com.example.smartexpense.ui.screens.AddExpenseScreen
import com.example.smartexpense.ui.screens.BudgetScreen
import com.example.smartexpense.ui.screens.ExpenseListScreen
import com.example.smartexpense.ui.screens.SettingsScreen
import com.example.smartexpense.ui.screens.SplashScreen
import com.example.smartexpense.ui.screens.SummaryScreen
import com.example.smartexpense.viewmodel.AddExpenseViewModel
import com.example.smartexpense.viewmodel.ExpenseListViewModel
import com.example.smartexpense.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SmartExpenseTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ExpenseNavHost(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Build your factory once and grab both ViewModels
    val app = LocalContext.current.applicationContext as android.app.Application
    val factory = ViewModelFactory(app)
    val listViewModel: ExpenseListViewModel = viewModel(factory = factory)


    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("expenseList") {
            ExpenseListScreen(
                viewModel       = listViewModel,
                onAddExpense    = { navController.navigate("addExpense") },
                onAddBudget     = { navController.navigate("budget") },
                onSettings      = { navController.navigate("settings") },
                onWeekSummary   = { s, e -> navController.navigate("summary/week/$s/$e") },
                onMonthSummary  = { s, e -> navController.navigate("summary/month/$s/$e") }
            )

        }
        composable("addExpense") {
            val addViewModel: AddExpenseViewModel = viewModel(factory = factory)
            AddExpenseScreen(
                viewModel = addViewModel,
                onSaved   = {
                    listViewModel.loadExpenses()
                    navController.popBackStack()
                }
            )
        }
        composable(
            route = "summary/week/{start}/{end}",
            arguments = listOf(
                navArgument("start"){ type = NavType.LongType },
                navArgument("end")  { type = NavType.LongType }
            )
        ) { back ->
            val s = back.arguments!!.getLong("start")
            val e = back.arguments!!.getLong("end")
            SummaryScreen(periodStart = s, periodEnd = e, isWeek = true)
        }

        composable(
            route = "summary/month/{start}/{end}",
            arguments = listOf(
                navArgument("start"){ type = NavType.LongType },
                navArgument("end")  { type = NavType.LongType }
            )
        ) { back ->
            val s = back.arguments!!.getLong("start")
            val e = back.arguments!!.getLong("end")
            SummaryScreen(periodStart = s, periodEnd = e, isWeek = false)
        }
        composable("budget") {
            BudgetScreen(onBack = { navController.popBackStack() })
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseNavHostPreview() {
    SmartExpenseTheme {
        ExpenseNavHost(navController = rememberNavController())
    }
}
