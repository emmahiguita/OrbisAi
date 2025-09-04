package com.example.orbisai.domain.usecases

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Typeface
import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.data.repository.TransactionRepository
import com.example.orbisai.domain.models.ReconciliationData
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

enum class ExportFormat(val extension: String, val mimeType: String) {
    PDF("pdf", "application/pdf"),
    CSV("csv", "text/csv"),
    EXCEL("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
}

class ExportReconciliationDataUseCase @Inject constructor(_repository: TransactionRepository) {
    
    fun exportReconciliationPdf(
        context: Context, 
        reconciliationData: ReconciliationData,
        fileName: String? = null,
        _format: ExportFormat = ExportFormat.PDF
    ): Result<File> {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            
            // Configurar paint para el texto
            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 20f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            val headerPaint = Paint().apply {
                color = Color.BLACK
                textSize = 14f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            val normalPaint = Paint().apply {
                color = Color.BLACK
                textSize = 10f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }
            
            val smallPaint = Paint().apply {
                color = Color.GRAY
                textSize = 8f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }
            
            // Margen y posiciones
            val margin = 50f
            var yPosition = margin + 50f
            
            // Título
            canvas.drawText("REPORTE DE CONCILIACIÓN BANCARIA", margin, yPosition, titlePaint)
            yPosition += 30f
            
            // Información de la empresa
            canvas.drawText("ORBIS AI - Sistema de Gestión Empresarial", margin, yPosition, headerPaint)
            yPosition += 30f
            
            // Línea separadora
            canvas.drawLine(margin, yPosition, 545f, yPosition, Paint().apply { 
                color = Color.BLACK 
                strokeWidth = 1f 
            })
            yPosition += 30f
            
            // Resumen de conciliación
            canvas.drawText("RESUMEN DE CONCILIACIÓN", margin, yPosition, headerPaint)
            yPosition += 20f
            
            canvas.drawText("Tasa de conciliación: ${reconciliationData.reconciliationRate.toInt()}%", margin, yPosition, normalPaint)
            yPosition += 15f
            
            canvas.drawText("Transacciones conciliadas: ${reconciliationData.reconciledTransactions.size}", margin, yPosition, normalPaint)
            yPosition += 15f
            
            canvas.drawText("Transacciones pendientes: ${reconciliationData.unreconciledTransactions.size}", margin, yPosition, normalPaint)
            yPosition += 30f
            
            // Línea separadora
            canvas.drawLine(margin, yPosition, 545f, yPosition, Paint().apply { 
                color = Color.BLACK 
                strokeWidth = 1f 
            })
            yPosition += 30f
            
            // Transacciones no conciliadas
            if (reconciliationData.unreconciledTransactions.isNotEmpty()) {
                canvas.drawText("TRANSACCIONES PENDIENTES DE CONCILIACIÓN", margin, yPosition, headerPaint)
                yPosition += 20f
                
                reconciliationData.unreconciledTransactions.take(10).forEach { transaction: FinancialTransaction ->
                    if (yPosition > 750f) {
                        // Crear nueva página si no hay espacio
                        pdfDocument.finishPage(page)
                        val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, 2).create()
                        val newPage = pdfDocument.startPage(newPageInfo)
                        val newCanvas = newPage.canvas
                        yPosition = margin
                        
                        // Continuar en nueva página
                        newCanvas.drawText("TRANSACCIONES PENDIENTES (continuación)", margin, yPosition, headerPaint)
                        yPosition += 20f
                    }
                    
                    canvas.drawText("• ${transaction.description} - ${transaction.amount}", margin, yPosition, normalPaint)
                    yPosition += 12f
                    canvas.drawText("  Fecha: ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(transaction.date)}", margin + 10f, yPosition, smallPaint)
                    yPosition += 12f
                }
                yPosition += 20f
            }
            
            // Transacciones conciliadas
            if (reconciliationData.reconciledTransactions.isNotEmpty()) {
                canvas.drawText("TRANSACCIONES CONCILIADAS", margin, yPosition, headerPaint)
                yPosition += 20f
                
                reconciliationData.reconciledTransactions.take(10).forEach { transaction: FinancialTransaction ->
                    if (yPosition > 750f) {
                        // Crear nueva página si no hay espacio
                        pdfDocument.finishPage(page)
                        val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, 3).create()
                        val newPage = pdfDocument.startPage(newPageInfo)
                        val newCanvas = newPage.canvas
                        yPosition = margin
                        
                        // Continuar en nueva página
                        newCanvas.drawText("TRANSACCIONES CONCILIADAS (continuación)", margin, yPosition, headerPaint)
                        yPosition += 20f
                    }
                    
                    canvas.drawText("• ${transaction.description} - ${transaction.amount}", margin, yPosition, normalPaint)
                    yPosition += 12f
                    canvas.drawText("  Referencia: ${transaction.reference ?: "N/A"}", margin + 10f, yPosition, smallPaint)
                    yPosition += 12f
                }
            }
            
            pdfDocument.finishPage(page)
            
            // Generar nombre de archivo
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(java.util.Date())
            val finalFileName = fileName ?: "reconciliation_report_$timestamp.pdf"
            
            // Guardar archivo
            val file = File(context.getExternalFilesDir(null), finalFileName)
            val outputStream = FileOutputStream(file)
            pdfDocument.writeTo(outputStream)
            pdfDocument.close()
            outputStream.close()
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun exportReconciliationCsv(
        context: Context,
        reconciliationData: ReconciliationData,
        fileName: String? = null
    ): Result<File> {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(java.util.Date())
            val finalFileName = fileName ?: "reconciliation_report_$timestamp.csv"
            val file = File(context.getExternalFilesDir(null), finalFileName)
            
            file.bufferedWriter().use { writer ->
                // Encabezados
                writer.write("Tipo,Descripción,Monto,Fecha,Referencia,Notas,Estado\n")
                
                // Transacciones no conciliadas
                reconciliationData.unreconciledTransactions.forEach { transaction: FinancialTransaction ->
                    writer.write("Pendiente,${transaction.description},${transaction.amount},${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(transaction.date)},${transaction.reference ?: ""},${transaction.notes ?: ""},No conciliada\n")
                }
                
                // Transacciones conciliadas
                reconciliationData.reconciledTransactions.forEach { transaction: FinancialTransaction ->
                    writer.write("Conciliada,${transaction.description},${transaction.amount},${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(transaction.date)},${transaction.reference ?: ""},${transaction.notes ?: ""},Conciliada\n")
                }
            }
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
