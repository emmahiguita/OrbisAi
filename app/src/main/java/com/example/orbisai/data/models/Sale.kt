package com.example.orbisai.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.orbisai.data.local.converters.DateConverter
import java.util.Date

@Entity(tableName = "sales")
@TypeConverters(DateConverter::class)
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val amount: Double,
    val client: String,
    val status: SaleStatus = SaleStatus.PENDING,
    val saleDate: Date,
    val dueDate: Date? = null,
    val commission: Double = 0.0,
    val notes: String? = null,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class SaleStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    REFUNDED
}
