package com.example.orbisai.data.repository

import com.example.orbisai.data.local.dao.TransactionDao
import com.example.orbisai.data.local.dao.InvoiceDao
import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.data.models.Invoice
import com.example.orbisai.domain.models.TransactionType
import com.example.orbisai.domain.models.TransactionStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao,
    private val invoiceDao: InvoiceDao
) : TransactionRepository {

    override fun getAllTransactions(): Flow<List<FinancialTransaction>> {
        return transactionDao.getAllTransactions()
    }

    override fun getTransactionsByType(type: TransactionType): Flow<List<FinancialTransaction>> {
        return transactionDao.getTransactionsByType(type)
    }

    override fun getTransactionsByStatus(status: TransactionStatus): Flow<List<FinancialTransaction>> {
        return transactionDao.getTransactionsByStatus(status)
    }

    override fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<FinancialTransaction>> {
        return transactionDao.getTransactionsByDateRange(startDate, endDate)
    }

    override fun getTransactionsByCategory(category: String): Flow<List<FinancialTransaction>> {
        return transactionDao.getTransactionsByCategory(category)
    }

    override fun getTotalByType(type: TransactionType): Flow<Double?> {
        return transactionDao.getTotalByType(type)
    }

    override fun getTotalByTypeAndDateRange(type: TransactionType, startDate: Date, endDate: Date): Flow<Double?> {
        return transactionDao.getTotalByTypeAndDateRange(type, startDate, endDate)
    }

    override suspend fun insertTransaction(transaction: FinancialTransaction): Long {
        return transactionDao.insertTransaction(transaction)
    }

    override suspend fun updateTransaction(transaction: FinancialTransaction) {
        transactionDao.updateTransaction(transaction)
    }

    override suspend fun deleteTransaction(transaction: FinancialTransaction) {
        transactionDao.deleteTransaction(transaction)
    }

    override suspend fun deleteTransactionById(id: Long) {
        transactionDao.deleteTransactionById(id)
    }
    
    override suspend fun deleteAllTransactions(): Int {
        return transactionDao.deleteAllTransactions()
    }

    override fun getTransactionCount(): Flow<Int> {
        return transactionDao.getTransactionCount()
    }
    
    override fun getTransactionById(id: Long): Flow<FinancialTransaction?> {
        return transactionDao.getTransactionById(id)
    }
    
    // Implementación de métodos para facturas
    override fun getAllInvoices(): Flow<List<Invoice>> {
        return invoiceDao.getAllInvoices()
    }
    
    override fun getInvoicesByStatus(status: com.example.orbisai.domain.models.InvoiceStatus): Flow<List<Invoice>> {
        return invoiceDao.getInvoicesByStatus(status)
    }
    
    override fun searchInvoices(query: String): Flow<List<Invoice>> {
        return invoiceDao.searchInvoices(query)
    }
    
    override fun getInvoicesByDateRange(startDate: Date, endDate: Date): Flow<List<Invoice>> {
        return invoiceDao.getInvoicesByIssueDateRange(startDate, endDate)
    }
    
    override fun getOverdueInvoices(): Flow<List<Invoice>> {
        return invoiceDao.getOverdueInvoices(Date())
    }
    
    override fun getInvoiceById(invoiceId: Long): Flow<Invoice?> {
        return invoiceDao.getInvoiceById(invoiceId)
    }
    
    override fun getInvoiceStatistics(): Flow<com.example.orbisai.data.models.InvoiceStatistics> {
        return invoiceDao.getInvoiceStatistics()
    }
    
    override suspend fun insertInvoice(invoice: Invoice): Long {
        return invoiceDao.insertInvoice(invoice)
    }
    
    override suspend fun updateInvoice(invoice: Invoice) {
        invoiceDao.updateInvoice(invoice)
    }
    
    override suspend fun deleteInvoice(invoice: Invoice) {
        invoiceDao.deleteInvoice(invoice)
    }
    
    override suspend fun updateInvoiceStatus(invoiceId: Long, status: com.example.orbisai.domain.models.InvoiceStatus) {
        invoiceDao.updateInvoiceStatus(invoiceId, status)
    }
    
    // Implementación de métodos para conciliación
    override fun getUnreconciledTransactions(): Flow<List<FinancialTransaction>> {
        return transactionDao.getUnreconciledTransactions()
    }
    
    override suspend fun markAsReconciled(transactionId: Long) {
        transactionDao.markAsReconciled(transactionId)
    }
}
