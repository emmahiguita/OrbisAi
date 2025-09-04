package com.example.orbisai.data.local.dao

import androidx.room.*
import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.domain.models.TransactionType
import com.example.orbisai.domain.models.TransactionStatus
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TransactionDao {
    @Query("SELECT * FROM financial_transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<FinancialTransaction>>

    @Query("SELECT * FROM financial_transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: TransactionType): Flow<List<FinancialTransaction>>

    @Query("SELECT * FROM financial_transactions WHERE status = :status ORDER BY date DESC")
    fun getTransactionsByStatus(status: TransactionStatus): Flow<List<FinancialTransaction>>

    @Query("SELECT * FROM financial_transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<FinancialTransaction>>

    @Query("SELECT * FROM financial_transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: String): Flow<List<FinancialTransaction>>

    @Query("SELECT SUM(amount) FROM financial_transactions WHERE type = :type")
    fun getTotalByType(type: TransactionType): Flow<Double?>

    @Query("SELECT SUM(amount) FROM financial_transactions WHERE type = :type AND date BETWEEN :startDate AND :endDate")
    fun getTotalByTypeAndDateRange(type: TransactionType, startDate: Date, endDate: Date): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinancialTransaction): Long

    @Update
    suspend fun updateTransaction(transaction: FinancialTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: FinancialTransaction)

    @Query("DELETE FROM financial_transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)
    
    @Query("DELETE FROM financial_transactions")
    suspend fun deleteAllTransactions(): Int

    @Query("SELECT COUNT(*) FROM financial_transactions")
    fun getTransactionCount(): Flow<Int>
    
    @Query("SELECT * FROM financial_transactions WHERE id = :id")
    fun getTransactionById(id: Long): Flow<FinancialTransaction?>
    
    @Query("SELECT * FROM financial_transactions WHERE reference IS NULL OR reference = ''")
    fun getUnreconciledTransactions(): Flow<List<FinancialTransaction>>
    
    @Query("UPDATE financial_transactions SET reference = 'RECONCILED' WHERE id = :transactionId")
    suspend fun markAsReconciled(transactionId: Long)
}
