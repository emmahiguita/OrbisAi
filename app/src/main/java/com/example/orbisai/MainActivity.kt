package com.example.orbisai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.orbisai.screens.FinanceScreen
import com.example.orbisai.screens.HomeScreen
import com.example.orbisai.screens.HRScreen
import com.example.orbisai.screens.SalesScreen
import com.example.orbisai.screens.SettingsScreen
import com.example.orbisai.ui.finance.FinanceInvoicesScreen
import com.example.orbisai.ui.finance.FinanceInvoiceDetailScreen
import com.example.orbisai.ui.finance.FinanceReconciliationScreen
import com.example.orbisai.ui.finance.FinanceReportsScreen
import com.example.orbisai.ui.finance.TransactionDetailScreen
import com.example.orbisai.ui.theme.OrbisAITheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            OrbisAITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavHost()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    
    // Optimizar la recolección de estado para evitar recomposiciones innecesarias
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Memoizar la lista de items para evitar recreaciones
    val navigationItems = remember {
        listOf(
            NavigationItem("home", "Inicio", Icons.Filled.Home),
            NavigationItem("finanzas", "Finanzas", Icons.Filled.AttachMoney),
            NavigationItem("hr", "RRHH", Icons.Filled.Group),
            NavigationItem("sales", "Ventas", Icons.Filled.ShoppingCart),
            NavigationItem("settings", "Ajustes", Icons.Filled.Settings)
        )
    }
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                navigationItems.forEach { item ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = item.icon, 
                                contentDescription = item.title,
                                modifier = Modifier.size(24.dp)
                            ) 
                        },
                        label = { 
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                                )
                            ) 
                        },
                        selected = isSelected,
                        onClick = {
                            // Navegación optimizada
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = modifier.padding(padding)
        ) {
            // ===== PANTALLA PRINCIPAL =====
            composable("home") { 
                HomeScreen() 
            }
            
            // ===== MÓDULO DE FINANZAS (COMPLETAMENTE IMPLEMENTADO) =====
            composable("finanzas") { 
                FinanceScreen(navController = navController)
            }
            composable("finance/invoices") { 
                FinanceInvoicesScreen(navController = navController)
            }
            composable("finance/reconciliation") { 
                FinanceReconciliationScreen(navController = navController)
            }
            composable("finance/reports") { 
                FinanceReportsScreen(navController = navController)
            }
            composable(
                route = "finance/invoice_detail/{invoiceId}",
                arguments = listOf(
                    navArgument("invoiceId") { 
                        type = NavType.LongType 
                    }
                )
            ) { backStackEntry ->
                val invoiceId = backStackEntry.arguments?.getLong("invoiceId") ?: 0L
                if (invoiceId > 0L) {
                    FinanceInvoiceDetailScreen(
                        navController = navController,
                        invoiceId = invoiceId
                    )
                }
            }
            composable(
                route = "finance/transaction_detail/{transactionId}",
                arguments = listOf(
                    navArgument("transactionId") { 
                        type = NavType.LongType 
                    }
                )
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0L
                if (transactionId > 0L) {
                    TransactionDetailScreen(
                        navController = navController,
                        transactionId = transactionId
                    )
                }
            }
            
            // ===== MÓDULOS IMPLEMENTADOS =====
            composable("hr") { 
                HRScreen() 
            }
            composable("sales") { 
                SalesScreen() 
            }
            composable("settings") { 
                SettingsScreen() 
            }
        }
    }
}

data class NavigationItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)