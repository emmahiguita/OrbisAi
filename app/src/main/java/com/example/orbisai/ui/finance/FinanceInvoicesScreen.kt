package com.example.orbisai.ui.finance

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.orbisai.data.models.Invoice
import com.example.orbisai.domain.models.InvoiceStatus
import com.example.orbisai.viewmodels.FinanceViewModel
import com.example.orbisai.ui.theme.OrbisAITheme
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceInvoicesScreen(
    navController: NavController,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }
    
    // Usar datos reales del ViewModel
    val invoices = uiState.filteredInvoices
    val selectedStatus = uiState.selectedInvoiceStatus
    val searchQuery = uiState.invoiceSearchQuery

    // Recargar datos cuando la pantalla se vuelve a mostrar
    LaunchedEffect(Unit) {
        viewModel.refreshData()
    }

    // Mostrar errores si los hay
    LaunchedEffect(uiState.invoiceError) {
        uiState.invoiceError?.let { error ->
            // Aquí podrías mostrar un Snackbar con el error
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Facturas",
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
                    IconButton(onClick = { showSearchDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar facturas"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Nueva factura",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Estados de carga y error
            if (uiState.isLoadingInvoices) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator()
                }
            } else if (uiState.invoiceError != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Error",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = uiState.invoiceError!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Filtros de estado
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Filtros
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                onClick = { viewModel.filterInvoicesByStatus(null) },
                                label = { Text("Todas") },
                                selected = selectedStatus == null
                            )
                            FilterChip(
                                onClick = { viewModel.filterInvoicesByStatus(InvoiceStatus.PENDING) },
                                label = { Text("Pendientes") },
                                selected = selectedStatus == InvoiceStatus.PENDING
                            )
                            FilterChip(
                                onClick = { viewModel.filterInvoicesByStatus(InvoiceStatus.APPROVED) },
                                label = { Text("Aprobadas") },
                                selected = selectedStatus == InvoiceStatus.APPROVED
                            )
                        }
                    }
                    
                    // Estado vacío
                    if (invoices.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No hay facturas registradas",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "Toca el botón + para agregar una nueva factura",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        // Lista de facturas
                        items(invoices) { invoice ->
                            InvoiceItem(
                                invoice = invoice,
                                onClick = {
                                    navController.navigate("finance/invoice_detail/${invoice.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
        
        // Dialog para agregar factura
        if (showAddDialog) {
            AddInvoiceDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { supplier, amount, description ->
                    viewModel.addInvoice(
                        invoiceNumber = "INV-${System.currentTimeMillis()}",
                        supplier = supplier,
                        amount = amount,
                        taxAmount = amount * 0.19, // 19% IVA
                        issueDate = Date(),
                        dueDate = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000),
                        description = description
                    )
                    showAddDialog = false
                }
            )
        }
        
        // Dialog para buscar facturas
        if (showSearchDialog) {
            SearchInvoicesDialog(
                currentQuery = searchQuery,
                onDismiss = { showSearchDialog = false },
                onSearch = { query ->
                    viewModel.searchInvoices(query)
                    showSearchDialog = false
                }
            )
        }
    }
}

@Composable
private fun InvoiceItem(
    invoice: Invoice,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = invoice.invoiceNumber,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = invoice.supplier,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    invoice.description?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${invoice.totalAmount.toInt()}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = getStatusDisplayName(invoice.status),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = getStatusColor(invoice.status)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddInvoiceDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String) -> Unit
) {
    var supplier by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Nueva Factura")
        },
        text = {
            Column {
                TextField(
                    value = supplier,
                    onValueChange = { supplier = it },
                    label = { Text("Proveedor") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    amount.toDoubleOrNull()?.let { amountValue ->
                        onConfirm(supplier, amountValue, description)
                    }
                },
                enabled = supplier.isNotBlank() && amount.toDoubleOrNull()?.let { it > 0 } == true
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun SearchInvoicesDialog(
    currentQuery: String,
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf(currentQuery) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Buscar Facturas")
        },
        text = {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Término de búsqueda") },
                placeholder = { Text("Proveedor, número, descripción...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onSearch(searchQuery) },
                enabled = searchQuery.isNotBlank()
            ) {
                Text("Buscar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

private fun getStatusDisplayName(status: InvoiceStatus): String {
    return when (status) {
        InvoiceStatus.PENDING -> "Pendiente"
        InvoiceStatus.APPROVED -> "Aprobada"
        InvoiceStatus.PAID -> "Pagada"
        InvoiceStatus.REJECTED -> "Rechazada"
        InvoiceStatus.OVERDUE -> "Vencida"
    }
}

private fun getStatusColor(status: InvoiceStatus): androidx.compose.ui.graphics.Color {
    return when (status) {
        InvoiceStatus.PENDING -> androidx.compose.ui.graphics.Color(0xFFFF9800) // Orange
        InvoiceStatus.APPROVED -> androidx.compose.ui.graphics.Color(0xFF4CAF50) // Green
        InvoiceStatus.PAID -> androidx.compose.ui.graphics.Color(0xFF2196F3) // Blue
        InvoiceStatus.REJECTED -> androidx.compose.ui.graphics.Color(0xFFF44336) // Red
        InvoiceStatus.OVERDUE -> androidx.compose.ui.graphics.Color(0xFFE91E63) // Pink
    }
}

@Composable
@Preview(showBackground = true, device = "id:pixel_5")
fun FinanceInvoicesScreenPreview() {
    OrbisAITheme {
        FinanceInvoicesScreen(
            navController = rememberNavController()
        )
    }
}
