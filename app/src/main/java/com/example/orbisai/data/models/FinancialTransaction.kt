package com.example.orbisai.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.orbisai.data.local.converters.DateConverter
import com.example.orbisai.domain.models.TransactionType
import com.example.orbisai.domain.models.TransactionStatus
import java.util.Date

@Entity(tableName = "financial_transactions")
@TypeConverters(DateConverter::class)
data class FinancialTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Date,
    val status: TransactionStatus = TransactionStatus.PENDING,
    val reference: String? = null,
    val notes: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)
