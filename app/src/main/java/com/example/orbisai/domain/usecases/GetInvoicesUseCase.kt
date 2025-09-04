package com.example.orbisai.domain.usecases

import com.example.orbisai.data.models.Invoice
import com.example.orbisai.data.models.InvoiceStatistics
import com.example.orbisai.data.repository.TransactionRepository
import com.example.orbisai.domain.models.InvoiceStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject

class GetInvoicesUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    
    fun getAllInvoices(): Flow<List<Invoice>> {
        return repository.getAllInvoices()
    }
    
    fun getInvoicesByStatus(status: InvoiceStatus): Flow<List<Invoice>> {
        return repository.getInvoicesByStatus(status)
    }
    
    fun searchInvoices(query: String): Flow<List<Invoice>> {
        return repository.searchInvoices(query)
    }
    
    fun getInvoicesByDateRange(startDate: Date, endDate: Date): Flow<List<Invoice>> {
        return repository.getInvoicesByDateRange(startDate, endDate)
    }
    
    fun getOverdueInvoices(): Flow<List<Invoice>> {
        return repository.getOverdueInvoices()
    }
    
    fun getInvoiceStatistics(): Flow<InvoiceStatistics> {
        return repository.getInvoiceStatistics()
    }
    
    fun getInvoiceById(invoiceId: Long): Flow<Invoice?> {
        return repository.getInvoiceById(invoiceId)
    }
}
