package com.example.orbisai.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.orbisai.ui.components.DashboardCard
import com.example.orbisai.ui.components.StatCard
import com.example.orbisai.ui.theme.OrbisAITheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Estado local para la pantalla de finanzas
    var totalIncome by rememberSaveable { mutableStateOf(12500.0) }
    var totalExpenses by rememberSaveable { mutableStateOf(8200.0) }
    var balance by rememberSaveable { mutableStateOf(totalIncome - totalExpenses) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showAddTransactionDialog by rememberSaveable { mutableStateOf(false) }
    
    // Lista de transacciones de ejemplo
    val transactions = remember {
        listOf(
            TransactionItem(
                id = 1L,
                description = "Venta Producto A",
                category = "Ventas",
                amount = 2500.0,
                type = TransactionType.INCOME,
                date = Date()
            ),
            TransactionItem(
                id = 2L,
                description = "Compra Materiales",
                category = "Compras",
                amount = 1200.0,
                type = TransactionType.EXPENSE,
                date = Date()
            ),
            TransactionItem(
                id = 3L,
                description = "Servicio Cliente B",
                category = "Servicios",
                amount = 1800.0,
                type = TransactionType.INCOME,
                date = Date()
            ),
            TransactionItem(
                id = 4L,
                description = "Gastos Administrativos",
                category = "Administración",
                amount = 500.0,
                type = TransactionType.EXPENSE,
                date = Date()
            )
        )
    }
    
    // Filtrar transacciones basado en búsqueda
    val filteredTransactions = remember(searchQuery, transactions) {
        if (searchQuery.isBlank()) {
            transactions
        } else {
            transactions.filter { transaction ->
                transaction.description.contains(searchQuery, ignoreCase = true) ||
                transaction.category.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Finanzas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Implementar búsqueda */ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar transacciones"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTransactionDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva transacción"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumen financiero
            item {
                Column {
                    Text(
                        text = "Resumen Financiero",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Ingresos",
                            value = "$${totalIncome.toInt()}",
                            subtitle = "Este mes",
                            icon = Icons.Default.TrendingUp,
                            modifier = Modifier.weight(1f)
                        )
                        
                        StatCard(
                            title = "Gastos",
                            value = "$${totalExpenses.toInt()}",
                            subtitle = "Este mes",
                            icon = Icons.Default.TrendingDown,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    StatCard(
                        title = "Balance",
                        value = "$${balance.toInt()}",
                        subtitle = "Neto",
                        icon = Icons.Default.AccountBalance
                    )
                }
            }
            
            // KPIs
            item {
                Column {
                    Text(
                        text = "Indicadores Clave",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DashboardCard(
                            item = "Ventas",
                            icon = Icons.Default.ShoppingCart,
                            value = "$${(totalIncome * 0.8).toInt()}",
                            modifier = Modifier.weight(1f)
                        )
                        
                        DashboardCard(
                            item = "Servicios",
                            icon = Icons.Default.Build,
                            value = "$${(totalIncome * 0.2).toInt()}",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            
            // Botones de acción rápida
            item {
                Column {
                    Text(
                        text = "Acciones Rápidas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { navController.navigate("finance/invoices") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                "Facturas",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                        
                        Button(
                            onClick = { navController.navigate("finance/reconciliation") },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                "Conciliar",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { navController.navigate("finance/reports") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            "Ver Reportes",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
            
            // Transacciones recientes
            item {
                Column {
                    Text(
                        text = "Transacciones Recientes",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            // Lista de transacciones
            items(filteredTransactions.take(5)) { transaction ->
                TransactionItemCard(
                    transaction = transaction,
                    onClick = {
                        navController.navigate("finance/transaction_detail/${transaction.id}")
                    }
                )
            }
        }
    }
}

@Composable
private fun TransactionItemCard(
    transaction: TransactionItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono según tipo
            Icon(
                imageVector = when (transaction.type) {
                    TransactionType.INCOME -> Icons.Default.TrendingUp
                    TransactionType.EXPENSE -> Icons.Default.TrendingDown
                },
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = when (transaction.type) {
                    TransactionType.INCOME -> MaterialTheme.colorScheme.primary
                    TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                }
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Información de la transacción
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = transaction.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Monto
            Text(
                text = "${if (transaction.type == TransactionType.INCOME) "+" else "-"}$${transaction.amount.toInt()}",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = when (transaction.type) {
                    TransactionType.INCOME -> MaterialTheme.colorScheme.primary
                    TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

// Data classes para la pantalla
data class TransactionItem(
    val id: Long,
    val description: String,
    val category: String,
    val amount: Double,
    val type: TransactionType,
    val date: Date
)

enum class TransactionType {
    INCOME, EXPENSE
}

@Composable
@Preview(showBackground = true, device = "id:pixel_5")
fun FinanceScreenPreview() {
    OrbisAITheme {
        FinanceScreen(
            navController = rememberNavController()
        )
    }
}

