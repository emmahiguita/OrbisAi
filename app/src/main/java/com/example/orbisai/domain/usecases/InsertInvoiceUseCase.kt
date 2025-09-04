package com.example.orbisai.domain.usecases

import com.example.orbisai.data.models.Invoice
import com.example.orbisai.data.repository.TransactionRepository
import com.example.orbisai.domain.models.InvoiceStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class InsertInvoiceUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    
    suspend fun insertInvoice(invoice: Invoice): Result<Long> {
        return try {
            val id = repository.insertInvoice(invoice)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateInvoice(invoice: Invoice): Result<Unit> {
        return try {
            repository.updateInvoice(invoice)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteInvoice(invoice: Invoice): Result<Unit> {
        return try {
            repository.deleteInvoice(invoice)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateInvoiceStatus(invoiceId: Long, status: InvoiceStatus): Result<Unit> {
        return try {
            repository.updateInvoiceStatus(invoiceId, status)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun getInvoiceById(invoiceId: Long): Flow<Invoice?> {
        return repository.getInvoiceById(invoiceId)
    }
}
