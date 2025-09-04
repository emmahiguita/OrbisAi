package com.example.orbisai.ui.finance.data

import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.data.models.Invoice
import com.example.orbisai.domain.models.InvoiceStatus
import com.example.orbisai.domain.models.TransactionStatus
import com.example.orbisai.domain.models.TransactionType
import com.example.orbisai.domain.models.ReconciliationData
import com.example.orbisai.viewmodels.FinancialReport
import com.example.orbisai.viewmodels.CategorySummary
import com.example.orbisai.viewmodels.MonthlyData
import java.util.Date

object SampleData {
    
    val sampleTransactions = listOf(
        FinancialTransaction(
            id = 1L,
            description = "Venta de productos",
            amount = 2500.0,
            type = TransactionType.INCOME,
            category = "Ventas",
            date = Date(),
            status = TransactionStatus.APPROVED,
            reference = "REF-001",
            notes = "Venta realizada a cliente corporativo",
            createdAt = Date(),
            updatedAt = Date()
        ),
        FinancialTransaction(
            id = 2L,
            description = "Pago de servicios",
            amount = 800.0,
            type = TransactionType.EXPENSE,
            category = "Servicios",
            date = Date(),
            status = TransactionStatus.PENDING,
            reference = "REF-002",
            notes = "Pago de servicios de internet",
            createdAt = Date(),
            updatedAt = Date()
        ),
        FinancialTransaction(
            id = 3L,
            description = "Transferencia bancaria",
            amount = 1500.0,
            type = TransactionType.TRANSFER,
            category = "Transferencias",
            date = Date(),
            status = TransactionStatus.APPROVED,
            reference = "REF-003",
            notes = "Transferencia a cuenta de ahorros",
            createdAt = Date(),
            updatedAt = Date()
        )
    )
    
    val sampleInvoices = listOf(
        Invoice(
            id = 1L,
            invoiceNumber = "INV-001",
            supplier = "Proveedor A",
            amount = 5000.0,
            taxAmount = 950.0,
            totalAmount = 5950.0,
            issueDate = Date(),
            dueDate = Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000),
            status = InvoiceStatus.PENDING,
            description = "Servicios de desarrollo",
            createdAt = Date(),
            updatedAt = Date()
        ),
        Invoice(
            id = 2L,
            invoiceNumber = "INV-002",
            supplier = "Proveedor B",
            amount = 3000.0,
            taxAmount = 570.0,
            totalAmount = 3570.0,
            issueDate = Date(),
            dueDate = Date(System.currentTimeMillis() + 15L * 24 * 60 * 60 * 1000),
            status = InvoiceStatus.APPROVED,
            description = "Licencias de software",
            createdAt = Date(),
            updatedAt = Date()
        ),
        Invoice(
            id = 3L,
            invoiceNumber = "INV-003",
            supplier = "Proveedor C",
            amount = 2000.0,
            taxAmount = 380.0,
            totalAmount = 2380.0,
            issueDate = Date(),
            dueDate = Date(System.currentTimeMillis() + 7L * 24 * 60 * 60 * 1000),
            status = InvoiceStatus.PAID,
            description = "Servicios de hosting",
            createdAt = Date(),
            updatedAt = Date()
        )
    )
    
    val sampleReconciliationData = ReconciliationData(
        reconciledTransactions = listOf(
            FinancialTransaction(
                id = 1L,
                description = "Pago de servicios",
                amount = 500.0,
                type = TransactionType.EXPENSE,
                category = "Servicios",
                date = Date(),
                status = TransactionStatus.APPROVED,
                reference = "REF-001",
                notes = "Conciliado automáticamente",
                createdAt = Date(),
                updatedAt = Date()
            )
        ),
        unreconciledTransactions = listOf(
            FinancialTransaction(
                id = 2L,
                description = "Venta de productos",
                amount = 1000.0,
                type = TransactionType.INCOME,
                category = "Ventas",
                date = Date(),
                status = TransactionStatus.PENDING,
                reference = null,
                notes = null,
                createdAt = Date(),
                updatedAt = Date()
            )
        ),
        reconciliationRate = 50.0
    )
    
    val sampleFinancialReport = FinancialReport(
        period = "Este Mes",
        totalIncome = 15000.0,
        totalExpenses = 8500.0,
        balance = 6500.0,
        transactionCount = 25,
        topCategories = listOf(
            CategorySummary("Desarrollo", 8000.0, 53.3),
            CategorySummary("Marketing", 4000.0, 26.7),
            CategorySummary("Infraestructura", 3000.0, 20.0)
        ),
        monthlyTrend = listOf(
            MonthlyData("Enero", 12000.0, 7000.0),
            MonthlyData("Febrero", 15000.0, 8500.0),
            MonthlyData("Marzo", 18000.0, 10000.0)
        )
    )
}
