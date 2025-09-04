package com.example.orbisai.data.repository

import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.data.models.Invoice
import com.example.orbisai.domain.models.TransactionType
import com.example.orbisai.domain.models.TransactionStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<FinancialTransaction>>
    fun getTransactionsByType(type: TransactionType): Flow<List<FinancialTransaction>>
    fun getTransactionsByStatus(status: TransactionStatus): Flow<List<FinancialTransaction>>
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<FinancialTransaction>>
    fun getTransactionsByCategory(category: String): Flow<List<FinancialTransaction>>
    fun getTotalByType(type: TransactionType): Flow<Double?>
    fun getTotalByTypeAndDateRange(type: TransactionType, startDate: Date, endDate: Date): Flow<Double?>
    suspend fun insertTransaction(transaction: FinancialTransaction): Long
    suspend fun updateTransaction(transaction: FinancialTransaction)
    suspend fun deleteTransaction(transaction: FinancialTransaction)
    suspend fun deleteTransactionById(id: Long)
    suspend fun deleteAllTransactions(): Int
    fun getTransactionCount(): Flow<Int>
    fun getTransactionById(id: Long): Flow<FinancialTransaction?>
    
    // Métodos para facturas
    fun getAllInvoices(): Flow<List<Invoice>>
    fun getInvoicesByStatus(status: com.example.orbisai.domain.models.InvoiceStatus): Flow<List<Invoice>>
    fun searchInvoices(query: String): Flow<List<Invoice>>
    fun getInvoicesByDateRange(startDate: Date, endDate: Date): Flow<List<Invoice>>
    fun getOverdueInvoices(): Flow<List<Invoice>>
    fun getInvoiceById(invoiceId: Long): Flow<Invoice?>
    fun getInvoiceStatistics(): Flow<com.example.orbisai.data.models.InvoiceStatistics>
    suspend fun insertInvoice(invoice: Invoice): Long
    suspend fun updateInvoice(invoice: Invoice)
    suspend fun deleteInvoice(invoice: Invoice)
    suspend fun updateInvoiceStatus(invoiceId: Long, status: com.example.orbisai.domain.models.InvoiceStatus)
    
    // Métodos para conciliación
    fun getUnreconciledTransactions(): Flow<List<FinancialTransaction>>
    suspend fun markAsReconciled(transactionId: Long)
}
