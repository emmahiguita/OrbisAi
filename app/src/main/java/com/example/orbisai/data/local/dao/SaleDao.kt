package com.example.orbisai.data.local.dao

import androidx.room.*
import com.example.orbisai.data.models.Sale
import com.example.orbisai.data.models.SaleStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface SaleDao {
    
    @Query("SELECT * FROM sales ORDER BY saleDate DESC")
    fun getAllSales(): Flow<List<Sale>>
    
    @Query("SELECT * FROM sales WHERE status = :status ORDER BY saleDate DESC")
    fun getSalesByStatus(status: SaleStatus): Flow<List<Sale>>
    
    @Query("SELECT * FROM sales WHERE client LIKE '%' || :client || '%' ORDER BY saleDate DESC")
    fun getSalesByClient(client: String): Flow<List<Sale>>
    
    @Query("SELECT * FROM sales WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR client LIKE '%' || :query || '%' ORDER BY saleDate DESC")
    fun searchSales(query: String): Flow<List<Sale>>
    
    @Query("SELECT * FROM sales WHERE id = :saleId")
    fun getSaleById(saleId: Long): Flow<Sale?>
    
    @Query("SELECT COUNT(*) FROM sales WHERE status = :status")
    fun getSaleCountByStatus(status: SaleStatus): Flow<Int>
    
    @Query("SELECT COUNT(*) FROM sales")
    fun getTotalSaleCount(): Flow<Int>
    
    @Query("SELECT SUM(amount) FROM sales WHERE status = :status")
    fun getTotalAmountByStatus(status: SaleStatus): Flow<Double?>
    
    @Query("SELECT SUM(amount) FROM sales")
    fun getTotalSalesAmount(): Flow<Double?>
    
    @Query("SELECT SUM(commission) FROM sales WHERE status = :status")
    fun getTotalCommissionByStatus(status: SaleStatus): Flow<Double?>
    
    @Query("SELECT * FROM sales WHERE saleDate BETWEEN :startDate AND :endDate ORDER BY saleDate DESC")
    fun getSalesByDateRange(startDate: Date, endDate: Date): Flow<List<Sale>>
    
    @Query("SELECT * FROM sales WHERE dueDate IS NOT NULL AND dueDate < :date ORDER BY dueDate ASC")
    fun getOverdueSales(date: Date): Flow<List<Sale>>
    
    @Query("SELECT * FROM sales WHERE amount >= :minAmount ORDER BY amount DESC")
    fun getSalesByMinAmount(minAmount: Double): Flow<List<Sale>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: Sale): Long
    
    @Update
    suspend fun updateSale(sale: Sale)
    
    @Delete
    suspend fun deleteSale(sale: Sale)
    
    @Query("DELETE FROM sales WHERE id = :saleId")
    suspend fun deleteSaleById(saleId: Long)
    
    @Query("DELETE FROM sales")
    suspend fun deleteAllSales()
    
    @Query("UPDATE sales SET status = :status WHERE id = :saleId")
    suspend fun updateSaleStatus(saleId: Long, status: SaleStatus)
    
    @Query("UPDATE sales SET amount = :amount WHERE id = :saleId")
    suspend fun updateSaleAmount(saleId: Long, amount: Double)
    
    @Query("UPDATE sales SET commission = :commission WHERE id = :saleId")
    suspend fun updateSaleCommission(saleId: Long, commission: Double)
}
