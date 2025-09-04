package com.example.orbisai.domain.usecases

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument.PageInfo
import com.example.orbisai.data.models.Invoice
import com.example.orbisai.data.repository.TransactionRepository
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class GenerateInvoicePdfUseCase @Inject constructor(_repository: TransactionRepository) {
    
    fun generateInvoicePdf(context: Context, invoice: Invoice): Result<File> {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas
            
            // Configurar paint para el texto
            val titlePaint = Paint().apply {
                color = Color.BLACK
                textSize = 24f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            val headerPaint = Paint().apply {
                color = Color.BLACK
                textSize = 16f
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                isAntiAlias = true
            }
            
            val normalPaint = Paint().apply {
                color = Color.BLACK
                textSize = 12f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }
            
            val smallPaint = Paint().apply {
                color = Color.GRAY
                textSize = 10f
                typeface = Typeface.DEFAULT
                isAntiAlias = true
            }
            
            // Margen y posiciones
            val margin = 50f
            var yPosition = margin + 50f
            
            // Título
            canvas.drawText("FACTURA", margin, yPosition, titlePaint)
            yPosition += 40f
            
            // Información de la empresa
            canvas.drawText("ORBIS AI", margin, yPosition, headerPaint)
            yPosition += 20f
            canvas.drawText("Sistema de Gestión Empresarial", margin, yPosition, normalPaint)
            yPosition += 30f
            
            // Línea separadora
            canvas.drawLine(margin, yPosition, 545f, yPosition, Paint().apply { 
                color = Color.BLACK 
                strokeWidth = 1f 
            })
            yPosition += 30f
            
            // Información de la factura
            canvas.drawText("Número de Factura:", margin, yPosition, headerPaint)
            canvas.drawText(invoice.invoiceNumber, 200f, yPosition, normalPaint)
            yPosition += 20f
            
            canvas.drawText("Fecha de Emisión:", margin, yPosition, headerPaint)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            canvas.drawText(dateFormat.format(invoice.issueDate), 200f, yPosition, normalPaint)
            yPosition += 20f
            
            canvas.drawText("Fecha de Vencimiento:", margin, yPosition, headerPaint)
            canvas.drawText(dateFormat.format(invoice.dueDate), 200f, yPosition, normalPaint)
            yPosition += 30f
            
            // Información del proveedor
            canvas.drawText("PROVEEDOR", margin, yPosition, headerPaint)
            yPosition += 20f
            canvas.drawText(invoice.supplier, margin, yPosition, normalPaint)
            yPosition += 30f
            
            // Línea separadora
            canvas.drawLine(margin, yPosition, 545f, yPosition, Paint().apply { 
                color = Color.BLACK 
                strokeWidth = 1f 
            })
            yPosition += 30f
            
            // Detalles de la factura
            canvas.drawText("DESCRIPCIÓN", margin, yPosition, headerPaint)
            canvas.drawText("MONTO", 400f, yPosition, headerPaint)
            yPosition += 20f
            
            // Línea separadora
            canvas.drawLine(margin, yPosition, 545f, yPosition, Paint().apply { 
                color = Color.GRAY 
                strokeWidth = 0.5f 
            })
            yPosition += 20f
            
            // Descripción o concepto
            val description = invoice.description ?: "Servicios prestados"
            canvas.drawText(description, margin, yPosition, normalPaint)
            canvas.drawText("$${String.format("%,.0f", invoice.amount)}", 400f, yPosition, normalPaint)
            yPosition += 20f
            
            // Línea separadora
            canvas.drawLine(margin, yPosition, 545f, yPosition, Paint().apply { 
                color = Color.GRAY 
                strokeWidth = 0.5f 
            })
            yPosition += 30f
            
            // Totales
            canvas.drawText("Subtotal:", 350f, yPosition, headerPaint)
            canvas.drawText("$${String.format("%,.0f", invoice.amount)}", 450f, yPosition, normalPaint)
            yPosition += 20f
            
            canvas.drawText("IVA (19%):", 350f, yPosition, headerPaint)
            canvas.drawText("$${String.format("%,.0f", invoice.taxAmount)}", 450f, yPosition, normalPaint)
            yPosition += 20f
            
            // Línea separadora
            canvas.drawLine(350f, yPosition, 545f, yPosition, Paint().apply { 
                color = Color.BLACK 
                strokeWidth = 1f 
            })
            yPosition += 20f
            
            canvas.drawText("TOTAL:", 350f, yPosition, headerPaint)
            canvas.drawText("$${String.format("%,.0f", invoice.totalAmount)}", 450f, yPosition, normalPaint)
            yPosition += 40f
            
            // Estado de la factura
            val statusText = when (invoice.status.name) {
                "PENDING" -> "PENDIENTE"
                "APPROVED" -> "APROBADA"
                "PAID" -> "PAGADA"
                "REJECTED" -> "RECHAZADA"
                "OVERDUE" -> "VENCIDA"
                else -> "DESCONOCIDO"
            }
            
            canvas.drawText("Estado:", margin, yPosition, headerPaint)
            canvas.drawText(statusText, 200f, yPosition, normalPaint)
            yPosition += 30f
            
            // Pie de página
            canvas.drawLine(margin, yPosition, 545f, yPosition, Paint().apply { 
                color = Color.BLACK 
                strokeWidth = 1f 
            })
            yPosition += 20f
            
            canvas.drawText("Documento generado por Orbis AI", margin, yPosition, smallPaint)
            canvas.drawText("Fecha: ${dateFormat.format(invoice.createdAt)}", margin, yPosition + 15f, smallPaint)
            
            pdfDocument.finishPage(page)
            
            // Crear archivo PDF
            val fileName = "Factura_${invoice.invoiceNumber}_${dateFormat.format(invoice.issueDate)}.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            // Asegurar que el directorio existe
            file.parentFile?.mkdirs()
            
            val fileOutputStream = FileOutputStream(file)
            
            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()
            
            // Verificar que el archivo se creó correctamente
            if (!file.exists()) {
                throw Exception("No se pudo crear el archivo PDF")
            }
            
            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
