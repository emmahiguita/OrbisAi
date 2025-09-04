package com.example.orbisai.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.orbisai.domain.usecases.ExportFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDialog(
    onDismiss: () -> Unit,
    onExport: (fileName: String, format: ExportFormat) -> Unit,
    isExporting: Boolean = false
) {
    var fileName by remember { mutableStateOf("") }
    var selectedFormat by remember { mutableStateOf(ExportFormat.PDF) }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isExporting) onDismiss() },
        title = {
            Text(
                text = "Exportar Reporte",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo de nombre de archivo
                OutlinedTextField(
                    value = fileName,
                    onValueChange = { 
                        fileName = it
                        showError = false
                    },
                    label = { Text("Nombre del archivo") },
                    placeholder = { Text("Ej: Reporte_Conciliacion") },
                    singleLine = true,
                    isError = showError && fileName.isBlank(),
                    supportingText = {
                        if (showError && fileName.isBlank()) {
                            Text("El nombre del archivo es requerido")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Selector de formato
                Text(
                    text = "Formato de exportación",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ExportFormat.values().forEach { format ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(8.dp),
                            onClick = { selectedFormat = format }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedFormat == format,
                                    onClick = { selectedFormat = format }
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Icon(
                                    imageVector = when (format) {
                                        ExportFormat.PDF -> Icons.Default.PictureAsPdf
                                        ExportFormat.CSV -> Icons.Default.TableChart
                                        ExportFormat.EXCEL -> Icons.Default.TableChart
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = when (format) {
                                            ExportFormat.PDF -> "PDF Document"
                                            ExportFormat.CSV -> "CSV File"
                                            ExportFormat.EXCEL -> "Excel Spreadsheet"
                                        },
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                    Text(
                                        text = when (format) {
                                            ExportFormat.PDF -> "Documento PDF con formato profesional"
                                            ExportFormat.CSV -> "Archivo de texto separado por comas"
                                            ExportFormat.EXCEL -> "Hoja de cálculo Excel"
                                        },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fileName.isBlank()) {
                        showError = true
                        return@Button
                    }
                    onExport(fileName, selectedFormat)
                },
                enabled = !isExporting && fileName.isNotBlank()
            ) {
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isExporting) "Exportando..." else "Exportar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isExporting
            ) {
                Text("Cancelar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp)
    )
}
