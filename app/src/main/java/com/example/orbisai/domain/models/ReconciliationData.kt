package com.example.orbisai.domain.models

import com.example.orbisai.data.models.FinancialTransaction

data class ReconciliationData(
    val reconciledTransactions: List<FinancialTransaction>,
    val unreconciledTransactions: List<FinancialTransaction>,
    val reconciliationRate: Double
)
