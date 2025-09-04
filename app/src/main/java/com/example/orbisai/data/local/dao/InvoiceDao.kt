package com.example.orbisai.data.local.dao

import androidx.room.*
import com.example.orbisai.data.models.Invoice
import com.example.orbisai.domain.models.InvoiceStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices ORDER BY issueDate DESC")
    fun getAllInvoices(): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE status = :status ORDER BY issueDate DESC")
    fun getInvoicesByStatus(status: InvoiceStatus): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE supplier LIKE '%' || :supplier || '%' ORDER BY issueDate DESC")
    fun getInvoicesBySupplier(supplier: String): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE dueDate BETWEEN :startDate AND :endDate ORDER BY dueDate ASC")
    fun getInvoicesByDueDateRange(startDate: Date, endDate: Date): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE issueDate BETWEEN :startDate AND :endDate ORDER BY issueDate DESC")
    fun getInvoicesByIssueDateRange(startDate: Date, endDate: Date): Flow<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE dueDate < :date AND status = 'PENDING' ORDER BY dueDate ASC")
    fun getOverdueInvoices(date: Date): Flow<List<Invoice>>

    @Query("SELECT SUM(totalAmount) FROM invoices WHERE status = :status")
    fun getTotalAmountByStatus(status: InvoiceStatus): Flow<Double?>

    @Query("SELECT SUM(totalAmount) FROM invoices WHERE status = 'PENDING'")
    fun getTotalPendingAmount(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM invoices WHERE status = 'PENDING'")
    fun getPendingInvoiceCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Update
    suspend fun updateInvoice(invoice: Invoice)

    @Delete
    suspend fun deleteInvoice(invoice: Invoice)

    @Query("DELETE FROM invoices WHERE id = :id")
    suspend fun deleteInvoiceById(id: Long)

    @Query("UPDATE invoices SET status = :status WHERE id = :id")
    suspend fun updateInvoiceStatus(id: Long, status: InvoiceStatus)
    
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    fun getInvoiceById(invoiceId: Long): Flow<Invoice?>
    
    @Query("SELECT * FROM invoices WHERE invoiceNumber LIKE '%' || :query || '%' OR supplier LIKE '%' || :query || '%' ORDER BY issueDate DESC")
    fun searchInvoices(query: String): Flow<List<Invoice>>
    
    @Query("""
        SELECT 
            COUNT(*) as totalInvoices,
            SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as pendingInvoices,
            SUM(CASE WHEN status = 'APPROVED' THEN 1 ELSE 0 END) as approvedInvoices,
            SUM(CASE WHEN status = 'PAID' THEN 1 ELSE 0 END) as paidInvoices,
            SUM(CASE WHEN status = 'PENDING' THEN totalAmount ELSE 0 END) as totalPendingAmount,
            SUM(CASE WHEN status = 'APPROVED' THEN totalAmount ELSE 0 END) as totalApprovedAmount,
            SUM(CASE WHEN status = 'PAID' THEN totalAmount ELSE 0 END) as totalPaidAmount
        FROM invoices
    """)
    fun getInvoiceStatistics(): Flow<com.example.orbisai.data.models.InvoiceStatistics>
}
