package com.example.orbisai.domain.usecases

import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.data.models.Invoice
import com.example.orbisai.data.repository.TransactionRepository
import com.example.orbisai.domain.models.TransactionStatus
import com.example.orbisai.domain.models.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Date
import javax.inject.Inject

class ProcessTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    
    suspend operator fun invoke(
        description: String,
        amount: Double,
        type: TransactionType,
        category: String,
        date: Date = Date(),
        reference: String? = null,
        notes: String? = null
    ): Result<Long> {
        return try {
            // Validaciones de negocio
            validateTransaction(description, amount, type)
            
            // Crear transacción
            val transaction = FinancialTransaction(
                description = description,
                amount = amount,
                type = type,
                category = category,
                date = date,
                status = TransactionStatus.APPROVED, // Por defecto aprobada
                reference = reference,
                notes = notes
            )
            
            // Guardar en repositorio
            val id = transactionRepository.insertTransaction(transaction)
            Result.success(id)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun validateTransaction(description: String, amount: Double, _type: TransactionType) {
        when {
            description.isBlank() -> throw IllegalArgumentException("La descripción es requerida")
            amount <= 0 -> throw IllegalArgumentException("El monto debe ser mayor a 0")
            amount > 1000000 -> throw IllegalArgumentException("El monto excede el límite permitido")
        }
    }
    
    fun getTransactionsByType(type: TransactionType): Flow<List<FinancialTransaction>> {
        return transactionRepository.getTransactionsByType(type)
    }
    
    fun getTransactionsByDateRange(startDate: Date, endDate: Date): Flow<List<FinancialTransaction>> {
        return transactionRepository.getTransactionsByDateRange(startDate, endDate)
    }
    
    fun getTotalByType(type: TransactionType): Flow<Double?> {
        return transactionRepository.getTotalByType(type)
    }
    
    suspend fun deleteAllTransactions(): Result<Int> {
        return try {
            val deletedCount = transactionRepository.deleteAllTransactions()
            Result.success(deletedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markTransactionAsReconciled(transactionId: Long): Result<Unit> {
        return try {
            transactionRepository.markAsReconciled(transactionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateTransactionDetails(transactionId: Long, reference: String, notes: String): Result<Unit> {
        return try {
            // Obtener la transacción actual
            val currentTransaction = transactionRepository.getTransactionById(transactionId).first()
            if (currentTransaction != null) {
                // Crear transacción actualizada
                val updatedTransaction = currentTransaction.copy(
                    reference = reference,
                    notes = notes
                )
                // Actualizar en el repositorio
                transactionRepository.updateTransaction(updatedTransaction)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Transacción no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteTransactionById(id: Long): Result<Unit> {
        return try {
            transactionRepository.deleteTransactionById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Método para insertar factura
    suspend fun insertInvoice(invoice: Invoice): Result<Long> {
        return try {
            val id = transactionRepository.insertInvoice(invoice)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
