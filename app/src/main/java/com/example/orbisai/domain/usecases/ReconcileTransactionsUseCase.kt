package com.example.orbisai.domain.usecases

import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.data.repository.TransactionRepository
import com.example.orbisai.domain.models.ReconciliationData
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class ReconcileTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    
    suspend operator fun invoke(): ReconciliationData {
        val allTransactions: List<FinancialTransaction> = transactionRepository.getAllTransactions().first()
        
        // Lógica simple de conciliación basada en referencia
        val reconciledTransactions: MutableList<FinancialTransaction> = mutableListOf()
        val unreconciledTransactions: MutableList<FinancialTransaction> = mutableListOf()
        
        allTransactions.forEach { transaction: FinancialTransaction ->
            // Considerar conciliada si tiene referencia o notas
            val isReconciled: Boolean = transaction.reference?.isNotBlank() == true || 
                              transaction.notes?.isNotBlank() == true
            
            if (isReconciled) {
                reconciledTransactions.add(transaction)
            } else {
                unreconciledTransactions.add(transaction)
            }
        }
        
        // Calcular tasa de conciliación
        val reconciliationRate: Double = if (allTransactions.isNotEmpty()) {
            (reconciledTransactions.size.toDouble() / allTransactions.size) * 100
        } else 0.0
        
        return ReconciliationData(
            reconciledTransactions = reconciledTransactions,
            unreconciledTransactions = unreconciledTransactions,
            reconciliationRate = reconciliationRate
        )
    }
}
