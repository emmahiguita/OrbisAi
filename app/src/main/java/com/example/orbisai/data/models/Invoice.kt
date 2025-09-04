package com.example.orbisai.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.orbisai.data.local.converters.DateConverter
import com.example.orbisai.domain.models.InvoiceStatus
import java.util.Date

@Entity(tableName = "invoices")
@TypeConverters(DateConverter::class)
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val supplier: String,
    val amount: Double,
    val taxAmount: Double = 0.0,
    val totalAmount: Double = amount + taxAmount,
    val issueDate: Date,
    val dueDate: Date,
    val status: InvoiceStatus = InvoiceStatus.PENDING,
    val description: String? = null,
    val reference: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
