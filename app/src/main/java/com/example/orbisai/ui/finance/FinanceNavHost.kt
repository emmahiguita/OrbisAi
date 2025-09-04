package com.example.orbisai.ui.finance

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.orbisai.screens.FinanceScreen

@Composable
fun FinanceNavHost(
    navController: NavHostController,
    startDestination: String = FinanceRoutes.Home
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(FinanceRoutes.Home) { 
            FinanceScreen(navController = navController)
        }
        composable(FinanceRoutes.Invoices) { 
            FinanceInvoicesScreen(navController = navController) 
        }
        composable(
            route = "${FinanceRoutes.InvoiceDetail}/{invoiceId}",
            arguments = listOf(
                navArgument("invoiceId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getLong("invoiceId") ?: 0L
            FinanceInvoiceDetailScreen(navController = navController, invoiceId = invoiceId)
        }
        composable(FinanceRoutes.Reconciliation) { 
            FinanceReconciliationScreen(navController = navController) 
        }
        composable(
            route = "${FinanceRoutes.TransactionDetail}/{transactionId}",
            arguments = listOf(
                navArgument("transactionId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0L
            TransactionDetailScreen(navController = navController, transactionId = transactionId)
        }
        composable(FinanceRoutes.Reports) { 
            FinanceReportsScreen(navController = navController) 
        }
    }
}
