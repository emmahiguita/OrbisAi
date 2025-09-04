package com.example.orbisai.ui.finance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.domain.models.TransactionStatus
import com.example.orbisai.domain.models.TransactionType
import com.example.orbisai.ui.theme.OrbisAITheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("UnusedVariable")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceReconciliationScreen(navController: NavController) {
    // Estados de UI
    var showExportDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Estado reactivo para transacciones
    var unreconciledTransactions by remember {
        mutableStateOf(
            listOf(
                FinancialTransaction(
                    id = 1L,
                    description = "Venta de proyecto web",
                    amount = 5000.0,
                    type = TransactionType.INCOME,
                    category = "Ventas",
                    date = Date(),
                    status = TransactionStatus.PENDING,
                    reference = null,
                    notes = null,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                FinancialTransaction(
                    id = 2L,
                    description = "Pago de nómina",
                    amount = 3200.0,
                    type = TransactionType.EXPENSE,
                    category = "Personal",
                    date = Date(),
                    status = TransactionStatus.PENDING,
                    reference = null,
                    notes = null,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                FinancialTransaction(
                    id = 3L,
                    description = "Servicios de hosting",
                    amount = 150.0,
                    type = TransactionType.EXPENSE,
                    category = "Tecnología",
                    date = Date(),
                    status = TransactionStatus.PENDING,
                    reference = null,
                    notes = null,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                FinancialTransaction(
                    id = 4L,
                    description = "Compra de equipos",
                    amount = 2500.0,
                    type = TransactionType.EXPENSE,
                    category = "Equipos",
                    date = Date(),
                    status = TransactionStatus.PENDING,
                    reference = null,
                    notes = null,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                FinancialTransaction(
                    id = 5L,
                    description = "Servicios de consultoría",
                    amount = 3000.0,
                    type = TransactionType.INCOME,
                    category = "Servicios",
                    date = Date(),
                    status = TransactionStatus.PENDING,
                    reference = null,
                    notes = null,
                    createdAt = Date(),
                    updatedAt = Date()
                )
            )
        )
    }

    var reconciledTransactions by remember {
        mutableStateOf(
            listOf(
                FinancialTransaction(
                    id = 6L,
                    description = "Pago de factura eléctrica",
                    amount = 450.0,
                    type = TransactionType.EXPENSE,
                    category = "Servicios",
                    date = Date(),
                    status = TransactionStatus.RECONCILED,
                    reference = "REF-ELEC-001",
                    notes = "Factura de electricidad del mes",
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                FinancialTransaction(
                    id = 7L,
                    description = "Venta de licencias",
                    amount = 1200.0,
                    type = TransactionType.INCOME,
                    category = "Ventas",
                    date = Date(),
                    status = TransactionStatus.RECONCILED,
                    reference = "REF-LIC-001",
                    notes = "Venta de licencias de software",
                    createdAt = Date(),
                    updatedAt = Date()
                )
            )
        )
    }

    // Cálculo reactivo de datos de conciliación
    val reconciliationData = remember(reconciledTransactions, unreconciledTransactions) {
        val totalTransactions = reconciledTransactions.size + unreconciledTransactions.size
        val reconciliationRate = if (totalTransactions > 0) {
            (reconciledTransactions.size.toDouble() / totalTransactions) * 100
        } else 0.0
        
        Triple(
            reconciliationRate,
            reconciledTransactions.size,
            unreconciledTransactions.size
        )
    }

    // Filtrar transacciones basado en búsqueda
    val filteredUnreconciled = unreconciledTransactions.filter { transaction ->
        searchQuery.isEmpty() || 
        transaction.description.contains(searchQuery, ignoreCase = true) ||
        transaction.category.contains(searchQuery, ignoreCase = true)
    }

    // Función para conciliar transacción directamente
    fun reconcileTransaction(transaction: FinancialTransaction) {
        try {
            println("🔄 Conciliando transacción: ${transaction.description}")
            
            // Generar referencia automática basada en la descripción
            val reference = "REF-${transaction.description.uppercase().replace(" ", "-").take(10)}-${System.currentTimeMillis() % 1000}"
            val notes = "Conciliado automáticamente el ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())}"
            
            // Crear transacción actualizada
            val updatedTransaction = transaction.copy(
                reference = reference,
                notes = notes,
                status = TransactionStatus.RECONCILED,
                updatedAt = Date()
            )
            
            println("✅ Transacción actualizada creada con referencia: $reference")
            
            // Actualizar listas de transacciones
            val newUnreconciled = unreconciledTransactions.filter { it.id != transaction.id }
            val newReconciled = reconciledTransactions + updatedTransaction
            
            unreconciledTransactions = newUnreconciled
            reconciledTransactions = newReconciled
            
            println("📊 Transacciones pendientes: ${unreconciledTransactions.size}")
            println("📊 Transacciones conciliadas: ${reconciledTransactions.size}")
            
            // Mostrar feedback de éxito
            snackbarMessage = "Transacción '${transaction.description}' conciliada exitosamente"
            showSnackbar = true
            
            println("✅ Conciliación completada exitosamente")
            
        } catch (e: Exception) {
            // Mostrar error si algo falla
            snackbarMessage = "Error al conciliar: ${e.message}"
            showSnackbar = true
            println("❌ Error al conciliar: ${e.message}")
            e.printStackTrace()
        }
    }

    // Función para exportar datos
    fun exportData(format: String) {
        snackbarMessage = "Datos exportados como $format exitosamente"
        showSnackbar = true
    }

    // Mostrar Snackbar
    LaunchedEffect(showSnackbar) {
        if (showSnackbar) {
            snackbarHostState.showSnackbar(
                message = snackbarMessage,
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
            showSnackbar = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Conciliación Bancaria",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showExportDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Exportar"
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
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Resumen de Conciliación
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Resumen de Conciliación",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        ReconciliationSummaryRow(
                            label = "Tasa de conciliación:",
                            value = "${String.format("%.1f", reconciliationData.first)}%"
                        )
                        
                        ReconciliationSummaryRow(
                            label = "Transacciones conciliadas:",
                            value = "${reconciliationData.second}"
                        )
                        
                        ReconciliationSummaryRow(
                            label = "Transacciones pendientes:",
                            value = "${reconciliationData.third}"
                        )
                    }
                }
            }

            // Barra de búsqueda
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Buscar por descripción o categoría...")
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Título de la sección
            item {
                Text(
                    text = "Transacciones Pendientes de Conciliación",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Lista de transacciones pendientes
            items(filteredUnreconciled) { transaction ->
                PendingTransactionCard(
                    transaction = transaction,
                    onReconcile = {
                        reconcileTransaction(transaction)
                    },
                    onViewDetails = { 
                        navController.navigate("finance/transaction_detail/${transaction.id}")
                    }
                )
            }

            // Mensaje si no hay transacciones pendientes
            if (filteredUnreconciled.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (searchQuery.isEmpty()) {
                                    "¡Excelente! Todas las transacciones están conciliadas"
                                } else {
                                    "No se encontraron transacciones que coincidan con la búsqueda"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Dialog para exportar datos
    if (showExportDialog) {
        ExportDialog(
            onDismiss = { showExportDialog = false },
            onExport = { format ->
                exportData(format)
                showExportDialog = false
            }
        )
    }
}

@Composable
private fun ReconciliationSummaryRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PendingTransactionCard(
    transaction: FinancialTransaction,
    onReconcile: () -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Información principal de la transacción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "$${transaction.amount} - ${transaction.category}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Badge de estado
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = "Pendiente",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onReconcile,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Conciliar",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
                
                OutlinedButton(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Detalles",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}



@Composable
private fun ExportDialog(
    onDismiss: () -> Unit,
    onExport: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Exportar Datos")
        },
        text = {
            Column {
                Text(
                    text = "Selecciona el formato de exportación:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                listOf("PDF", "CSV", "Excel").forEach { format ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onExport(format) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = format,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
@Preview(showBackground = true, device = "id:pixel_5")
fun FinanceReconciliationScreenPreview() {
    OrbisAITheme {
        FinanceReconciliationScreen(
            navController = rememberNavController()
        )
    }
}
