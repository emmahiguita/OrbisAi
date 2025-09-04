package com.example.orbisai.data.models

import androidx.room.Embedded
import androidx.room.Relation

data class InvoiceStatistics(
    val totalInvoices: Int,
    val pendingInvoices: Int,
    val approvedInvoices: Int,
    val paidInvoices: Int,
    val totalPendingAmount: Double,
    val totalApprovedAmount: Double,
    val totalPaidAmount: Double
)
