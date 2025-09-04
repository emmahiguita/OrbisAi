@file:Suppress("EnumValuesSoftDeprecate")

package com.example.orbisai.ui.finance

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.orbisai.data.models.Invoice
import com.example.orbisai.domain.models.InvoiceStatus
import com.example.orbisai.ui.theme.OrbisAITheme
import com.example.orbisai.viewmodels.FinanceViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Enum para formatos de exportación
enum class ExportFormat(val displayName: String, val icon: String, val extension: String) {
    PDF("PDF", "📄", "pdf"),
    EXCEL("Excel", "📊", "xlsx"),
    CSV("CSV", "📋", "csv"),
    JSON("JSON", "🔧", "json")
}

@Suppress("VariableNeverRead")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceInvoiceDetailScreen(
    navController: NavController,
    invoiceId: Long,
    viewModel: FinanceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showStatusDialog by remember { mutableStateOf(false) }
    var showExportFormatDialog by remember { mutableStateOf(false) }
    var showFileNameDialog by remember { mutableStateOf(false) }
    var selectedFormat by remember { mutableStateOf<ExportFormat?>(null) }
    var customFileName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // Obtener la factura del ViewModel o usar datos de ejemplo si no está disponible
    val invoice = uiState.selectedInvoice ?: remember {
        Invoice(
            id = invoiceId,
            invoiceNumber = "INV-${invoiceId.toString().padStart(3, '0')}",
            supplier = "Proveedor Ejemplo",
            amount = 5000.0,
            taxAmount = 950.0,
            totalAmount = 5950.0,
            issueDate = Date(),
            dueDate = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000),
            status = InvoiceStatus.PENDING,
            description = "Servicios de desarrollo y consultoría",
            createdAt = Date(),
            updatedAt = Date()
        )
    }

    // Cargar la factura cuando se abre la pantalla
    LaunchedEffect(invoiceId) {
        viewModel.getInvoiceById(invoiceId)
    }

    // Mostrar errores si los hay
    LaunchedEffect(uiState.invoiceError) {
        uiState.invoiceError?.let { error ->
            snackbarHostState.showSnackbar(
                message = error,
                duration = androidx.compose.material3.SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }

    // Mostrar confirmación de cambio de estado
    LaunchedEffect(uiState.isLoadingInvoices) {
        if (!uiState.isLoadingInvoices && uiState.selectedInvoice != null) {
            snackbarHostState.showSnackbar(
                message = "Estado de factura actualizado exitosamente",
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
        }
    }

    // Mostrar confirmación de PDF generado
    LaunchedEffect(uiState.pdfGenerated) {
        if (uiState.pdfGenerated) {
            snackbarHostState.showSnackbar(
                message = "Archivo generado exitosamente",
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
            viewModel.clearPdfState()
        }
    }

    // Función para exportar en diferentes formatos
    @Suppress("AssignedValueIsNeverRead")
    fun exportInvoice(format: ExportFormat, fileName: String? = null) {
        when (format) {
            ExportFormat.PDF -> viewModel.generateInvoicePdf(context)
            ExportFormat.EXCEL -> generateExcelFile(context, invoice, snackbarHostState, coroutineScope, fileName)
            ExportFormat.CSV -> generateCsvFile(context, invoice, snackbarHostState, coroutineScope, fileName)
            ExportFormat.JSON -> generateJsonFile(context, invoice, snackbarHostState, coroutineScope, fileName)
        }
        showExportFormatDialog = false
        showFileNameDialog = false
        selectedFormat = null
        customFileName = ""
    }

    // Función para manejar selección de formato
    fun onFormatSelected(format: ExportFormat) {
        selectedFormat = format
        if (format == ExportFormat.PDF) {
            // PDF usa el sistema del ViewModel, no necesita nombre personalizado
            exportInvoice(format)
        } else {
            // Para otros formatos, mostrar diálogo de nombre
            showFileNameDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = invoice.invoiceNumber,
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
                    IconButton(
                        onClick = { 
                            viewModel.generateInvoicePdf(context)
                        },
                        enabled = !uiState.isGeneratingPdf
                    ) {
                        if (uiState.isGeneratingPdf) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "Generar PDF"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Información principal
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Información General",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    InvoiceDetailRow("Número", invoice.invoiceNumber)
                    InvoiceDetailRow("Proveedor", invoice.supplier)
                    InvoiceDetailRow("Descripción", invoice.description ?: "Sin descripción")
                    InvoiceDetailRow("Estado", getStatusDisplayName(invoice.status), statusColor = getStatusColor(invoice.status))
                }
            }
            
            // Detalles financieros
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Detalles Financieros",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    InvoiceDetailRow("Monto Base", "$${invoice.amount.toInt()}")
                    InvoiceDetailRow("Impuestos", "$${invoice.taxAmount.toInt()}")
                    InvoiceDetailRow("Total", "$${invoice.totalAmount.toInt()}", true)
                }
            }
            
            // Fechas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Fechas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    InvoiceDetailRow("Fecha de Emisión", formatDate(invoice.issueDate))
                    InvoiceDetailRow("Fecha de Vencimiento", formatDate(invoice.dueDate))
                    
                    // Verificar si está vencida
                    if (invoice.dueDate.before(Date()) && invoice.status == InvoiceStatus.PENDING) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "⚠️ Esta factura está vencida",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            // Acciones
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Acciones",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { showStatusDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !uiState.isLoadingInvoices
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cambiar Estado de la Factura")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { showExportFormatDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exportar Factura")
                    }
                }
            }
        }
        
        // Dialog para cambiar estado
        if (showStatusDialog) {
            ChangeStatusDialog(
                currentStatus = invoice.status,
                onDismiss = { showStatusDialog = false },
                onStatusChanged = { newStatus ->
                    // Actualizar el estado en la base de datos
                    viewModel.updateInvoiceStatus(invoiceId, newStatus)
                    showStatusDialog = false
                }
            )
        }
        
        // Dialog para seleccionar formato de exportación
        if (showExportFormatDialog) {
            ExportFormatDialog(
                onDismiss = { showExportFormatDialog = false },
                onFormatSelected = { format ->
                    onFormatSelected(format)
                }
            )
        }
        
        // Dialog para ingresar nombre de archivo
        if (showFileNameDialog) {
            FileNameDialog(
                format = selectedFormat!!,
                defaultFileName = "Factura_${invoice.invoiceNumber}_${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(invoice.issueDate)}",
                onDismiss = { 
                    showFileNameDialog = false
                    selectedFormat = null
                },
                onConfirm = { fileName ->
                    exportInvoice(selectedFormat!!, fileName)
                }
            )
        }
    }
}

@Composable
private fun InvoiceDetailRow(
    label: String,
    value: String,
    isTotal: Boolean = false,
    statusColor: Color? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
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
            style = if (isTotal) {
                MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = when {
                isTotal -> MaterialTheme.colorScheme.primary
                statusColor != null -> statusColor
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@Composable
private fun ChangeStatusDialog(
    currentStatus: InvoiceStatus,
    onDismiss: () -> Unit,
    onStatusChanged: (InvoiceStatus) -> Unit
) {
    var selectedStatus by remember { mutableStateOf(currentStatus) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Cambiar Estado")
        },
        text = {
            Column {
                InvoiceStatus.values().forEach { status ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedStatus == status,
                            onClick = { selectedStatus = status }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = getStatusDisplayName(status),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onStatusChanged(selectedStatus) }
            ) {
                Text("Confirmar")
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
private fun ExportFormatDialog(
    onDismiss: () -> Unit,
    onFormatSelected: (ExportFormat) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Seleccionar Formato de Exportación")
        },
        text = {
            Column {
                ExportFormat.values().forEach { format ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { 
                                onFormatSelected(format)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = format.icon,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = format.displayName,
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
private fun FileNameDialog(
    format: ExportFormat,
    defaultFileName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var fileName by remember { mutableStateOf(defaultFileName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Nombre del Archivo")
        },
        text = {
            Column {
                Text(
                    text = "Ingresa el nombre para el archivo ${format.displayName}:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { fileName = it },
                    label = { Text("Nombre del archivo") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "El archivo se guardará como: ${fileName}.${format.extension}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    if (fileName.isNotBlank()) {
                        onConfirm(fileName)
                    }
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// Funciones de utilidad
private fun getStatusDisplayName(status: InvoiceStatus): String {
    return when (status) {
        InvoiceStatus.PENDING -> "Pendiente"
        InvoiceStatus.APPROVED -> "Aprobada"
        InvoiceStatus.PAID -> "Pagada"
        InvoiceStatus.REJECTED -> "Rechazada"
        InvoiceStatus.OVERDUE -> "Vencida"
    }
}

private fun getStatusColor(status: InvoiceStatus): Color {
    return when (status) {
        InvoiceStatus.PENDING -> Color(0xFFFF9800) // Orange
        InvoiceStatus.APPROVED -> Color(0xFF4CAF50) // Green
        InvoiceStatus.PAID -> Color(0xFF2196F3) // Blue
        InvoiceStatus.REJECTED -> Color(0xFFF44336) // Red
        InvoiceStatus.OVERDUE -> Color(0xFFE91E63) // Pink
    }
}

private fun formatDate(date: Date): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(date)
}

// Funciones de exportación
fun generateExcelFile(
    context: android.content.Context,
    invoice: Invoice,
    snackbarHostState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    customFileName: String? = null
) {
    try {
        val fileName = customFileName ?: "Factura_${invoice.invoiceNumber}_${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(invoice.issueDate)}"
        val file = File(context.getExternalFilesDir(null), "$fileName.xlsx")
        
        // Asegurar que el directorio existe
        file.parentFile?.mkdirs()
        
        val content = buildString {
            appendLine("Número de Factura,${invoice.invoiceNumber}")
            appendLine("Proveedor,${invoice.supplier}")
            appendLine("Descripción,${invoice.description ?: ""}")
            appendLine("Monto Base,${invoice.amount}")
            appendLine("Impuestos,${invoice.taxAmount}")
            appendLine("Total,${invoice.totalAmount}")
            appendLine("Fecha de Emisión,${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(invoice.issueDate)}")
            appendLine("Fecha de Vencimiento,${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(invoice.dueDate)}")
            appendLine("Estado,${getStatusDisplayName(invoice.status)}")
        }
        
        file.writeText(content)
        
        // Verificar que el archivo se creó correctamente
        if (file.exists() && file.length() > 0) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Archivo Excel guardado exitosamente",
                    duration = androidx.compose.material3.SnackbarDuration.Short
                )
            }
            shareFile(context, file, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Excel", invoice, snackbarHostState, coroutineScope)
        } else {
            throw Exception("No se pudo crear el archivo Excel")
        }
    } catch (e: Exception) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = "Error al generar Excel: ${e.message}",
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
        }
    }
}

fun generateCsvFile(
    context: android.content.Context,
    invoice: Invoice,
    snackbarHostState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    customFileName: String? = null
) {
    try {
        val fileName = customFileName ?: "Factura_${invoice.invoiceNumber}_${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(invoice.issueDate)}"
        val file = File(context.getExternalFilesDir(null), "$fileName.csv")
        
        // Asegurar que el directorio existe
        file.parentFile?.mkdirs()
        
        val content = buildString {
            appendLine("Número de Factura,${invoice.invoiceNumber}")
            appendLine("Proveedor,${invoice.supplier}")
            appendLine("Descripción,${invoice.description ?: ""}")
            appendLine("Monto Base,${invoice.amount}")
            appendLine("Impuestos,${invoice.taxAmount}")
            appendLine("Total,${invoice.totalAmount}")
            appendLine("Fecha de Emisión,${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(invoice.issueDate)}")
            appendLine("Fecha de Vencimiento,${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(invoice.dueDate)}")
            appendLine("Estado,${getStatusDisplayName(invoice.status)}")
        }
        
        file.writeText(content)
        
        // Verificar que el archivo se creó correctamente
        if (file.exists() && file.length() > 0) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Archivo CSV guardado exitosamente",
                    duration = androidx.compose.material3.SnackbarDuration.Short
                )
            }
            shareFile(context, file, "text/csv", "CSV", invoice, snackbarHostState, coroutineScope)
        } else {
            throw Exception("No se pudo crear el archivo CSV")
        }
    } catch (e: Exception) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = "Error al generar CSV: ${e.message}",
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
        }
    }
}

fun generateJsonFile(
    context: android.content.Context,
    invoice: Invoice,
    snackbarHostState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
    customFileName: String? = null
) {
    try {
        val fileName = customFileName ?: "Factura_${invoice.invoiceNumber}_${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(invoice.issueDate)}"
        val file = File(context.getExternalFilesDir(null), "$fileName.json")
        
        // Asegurar que el directorio existe
        file.parentFile?.mkdirs()
        
        val jsonContent = """
            {
                "numeroFactura": "${invoice.invoiceNumber}",
                "proveedor": "${invoice.supplier}",
                "descripcion": "${invoice.description ?: ""}",
                "montoBase": ${invoice.amount},
                "impuestos": ${invoice.taxAmount},
                "total": ${invoice.totalAmount},
                "fechaEmision": "${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(invoice.issueDate)}",
                "fechaVencimiento": "${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(invoice.dueDate)}",
                "estado": "${getStatusDisplayName(invoice.status)}"
            }
        """.trimIndent()
        
        file.writeText(jsonContent)
        
        // Verificar que el archivo se creó correctamente
        if (file.exists() && file.length() > 0) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Archivo JSON guardado exitosamente",
                    duration = androidx.compose.material3.SnackbarDuration.Short
                )
            }
            shareFile(context, file, "application/json", "JSON", invoice, snackbarHostState, coroutineScope)
        } else {
            throw Exception("No se pudo crear el archivo JSON")
        }
    } catch (e: Exception) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = "Error al generar JSON: ${e.message}",
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
        }
    }
}

fun shareFile(
    context: android.content.Context,
    file: File,
    mimeType: String,
    formatName: String,
    invoice: Invoice,
    snackbarHostState: SnackbarHostState,
    coroutineScope: kotlinx.coroutines.CoroutineScope
) {
    try {
        if (file.exists() && file.length() > 0) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Factura ${invoice.invoiceNumber} - $formatName")
                putExtra(Intent.EXTRA_TEXT, "Adjunto la factura ${invoice.invoiceNumber} en formato $formatName")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            // Verificar si hay aplicaciones disponibles para compartir
            val chooser = Intent.createChooser(shareIntent, "Compartir $formatName")
            if (shareIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(chooser)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "Archivo listo para compartir",
                        duration = androidx.compose.material3.SnackbarDuration.Short
                    )
                }
            } else {
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(
                        message = "No hay aplicaciones disponibles para compartir $formatName",
                        duration = androidx.compose.material3.SnackbarDuration.Short
                    )
                }
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "El archivo $formatName no existe o está vacío",
                    duration = androidx.compose.material3.SnackbarDuration.Short
                )
            }
        }
    } catch (e: Exception) {
        coroutineScope.launch {
            snackbarHostState.showSnackbar(
                message = "Error al compartir $formatName: ${e.message}",
                duration = androidx.compose.material3.SnackbarDuration.Short
            )
        }
    }
}

@Composable
@Preview(showBackground = true, device = "id:pixel_5")
fun FinanceInvoiceDetailScreenPreview() {
    OrbisAITheme {
        FinanceInvoiceDetailScreen(
            navController = rememberNavController(),
            invoiceId = 1L
        )
    }
}
