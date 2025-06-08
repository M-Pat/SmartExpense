@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smartexpense.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavHostController,
    delayMillis: Long = 2000L
) {
    // Full-screen surface using your theme’s background
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "SmartExpense",
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
                CircularProgressIndicator()
            }
        }
    }

    // Once mounted, wait then navigate
    LaunchedEffect(Unit) {
        delay(delayMillis)
        navController.navigate("expenseList") {
            // Remove "splash" from back-stack so back button won't return here
            popUpTo("splash") { inclusive = true }
        }
    }
}
