package com.example.orbisai.domain.usecases

import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.data.repository.TransactionRepository
import com.example.orbisai.domain.models.TransactionType
import com.example.orbisai.viewmodels.CategorySummary
import com.example.orbisai.viewmodels.FinancialReport
import com.example.orbisai.viewmodels.MonthlyData
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class GenerateFinancialReportUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {
    
    suspend operator fun invoke(): FinancialReport {
        val allTransactions: List<FinancialTransaction> = transactionRepository.getAllTransactions().first()
        val currentDate = Date()
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        
        // Calcular período (último mes)
        val period = SimpleDateFormat("MMMM yyyy", Locale.forLanguageTag("es-CO")).format(currentDate)
        
        // Calcular totales
        val totalIncome: Double = allTransactions.filter { transaction -> 
            transaction.type == TransactionType.INCOME 
        }.sumOf { transaction -> 
            transaction.amount 
        }
        
        val totalExpenses: Double = allTransactions.filter { transaction -> 
            transaction.type == TransactionType.EXPENSE 
        }.sumOf { transaction -> 
            transaction.amount 
        }
        
        val balance: Double = totalIncome - totalExpenses
        
        // Calcular categorías principales
        val categoryMap: Map<String, List<FinancialTransaction>> = allTransactions.groupBy { transaction -> 
            transaction.category 
        }
        
        val topCategories: List<CategorySummary> = categoryMap.map { entry ->
            val category: String = entry.key
            val transactions: List<FinancialTransaction> = entry.value
            val amount: Double = transactions.sumOf { transaction -> transaction.amount }
            val percentage: Double = if (totalIncome + totalExpenses > 0) {
                (amount / (totalIncome + totalExpenses)) * 100
            } else 0.0
            CategorySummary(category, amount, percentage)
        }.sortedByDescending { categorySummary -> categorySummary.amount }.take(5)
        
        // Calcular tendencia mensual (últimos 6 meses)
        val monthlyTrend: MutableList<MonthlyData> = mutableListOf()
        for (i in 5 downTo 0) {
            calendar.add(Calendar.MONTH, -1)
            val monthDate: Date = calendar.time
            val monthName: String = SimpleDateFormat("MMM", Locale.forLanguageTag("es-CO")).format(monthDate)
            
            val monthTransactions: List<FinancialTransaction> = allTransactions.filter { transaction -> 
                val transactionCalendar = Calendar.getInstance()
                transactionCalendar.time = transaction.date
                val month = transactionCalendar.get(Calendar.MONTH)
                val year = transactionCalendar.get(Calendar.YEAR)
                month == monthDate.month && year == monthDate.year + 1900
            }
            
            val monthIncome: Double = monthTransactions.filter { transaction -> 
                transaction.type == TransactionType.INCOME 
            }.sumOf { transaction -> 
                transaction.amount 
            }
            
            val monthExpenses: Double = monthTransactions.filter { transaction -> 
                transaction.type == TransactionType.EXPENSE 
            }.sumOf { transaction -> 
                transaction.amount 
            }
            
            monthlyTrend.add(MonthlyData(monthName, monthIncome, monthExpenses))
        }
        monthlyTrend.reverse()
        
        return FinancialReport(
            period = period,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            balance = balance,
            transactionCount = allTransactions.size,
            topCategories = topCategories,
            monthlyTrend = monthlyTrend
        )
    }
}
