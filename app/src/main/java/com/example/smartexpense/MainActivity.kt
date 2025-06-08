
package com.example.smartexpense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartexpense.ui.theme.SmartExpenseTheme
import com.example.smartexpense.ui.screens.ExpenseListScreen
import com.example.smartexpense.ui.screens.AddExpenseScreen
import com.example.smartexpense.ui.screens.SummaryScreen
import com.example.smartexpense.ui.screens.SettingsScreen
import com.example.smartexpense.ui.screens.SplashScreen


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
    NavHost(
        navController = navController,
        startDestination = "splash",
        modifier = modifier
    ) {
        composable("splash") {
            SplashScreen(navController = navController)
        }
        composable("expenseList") {
            ExpenseListScreen(onAddClicked = {
                navController.navigate("addExpense")
            })
        }
        composable("addExpense") {
            AddExpenseScreen(onSaved = {
                navController.popBackStack()
            })
        }
        composable("summary") {
            SummaryScreen()
        }
        composable("settings") {
            SettingsScreen()
        }
    }
}


@Composable
fun ExpenseListScreen(onAddClicked: () -> Unit) {
    // TODO: replace with your ExpenseListScreen implementation
    Text("Expense List Screen\nTap + to add new expense")
}

@Composable
fun AddExpenseScreen(onSaved: () -> Unit) {
    // TODO: replace with your AddExpenseScreen implementation
    Text("Add Expense Screen\nFill form and tap save")
}

@Composable
fun SummaryScreen() {
    // TODO: replace with your SummaryScreen implementation
    Text("Summary Screen\nYour expense charts here")
}

@Composable
fun SettingsScreen() {
    // TODO: replace with your SettingsScreen implementation
    Text("Settings Screen\nApp preferences here")
}

@Preview(showBackground = true)
@Composable
fun ExpenseNavHostPreview() {
    SmartExpenseTheme {
        ExpenseNavHost(navController = rememberNavController())
    }
}
