package com.example.orbisai.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.data.models.Invoice
import com.example.orbisai.domain.models.TransactionType
import com.example.orbisai.domain.models.InvoiceStatus
import com.example.orbisai.domain.usecases.ProcessTransactionUseCase
import com.example.orbisai.domain.usecases.GenerateFinancialReportUseCase
import com.example.orbisai.domain.usecases.GetInvoicesUseCase
import com.example.orbisai.domain.usecases.InsertInvoiceUseCase
import com.example.orbisai.domain.usecases.GenerateInvoicePdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.util.Date
import javax.inject.Inject

data class FinanceUiState(
    val transactions: List<FinancialTransaction> = emptyList(),
    val filteredTransactions: List<FinancialTransaction> = emptyList(),
    val invoices: List<Invoice> = emptyList(),
    val filteredInvoices: List<Invoice> = emptyList(),
    val selectedInvoice: Invoice? = null,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0,
    val financialReport: FinancialReport? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val invoiceSearchQuery: String = "", // For invoice search text
    val selectedInvoiceStatus: InvoiceStatus? = null, // For status filter
    // val isInvoiceSearchActive: Boolean = false, // Can be derived from invoiceSearchQuery.isNotBlank() or selectedInvoiceStatus != null
    val isLoading: Boolean = false,
    val isLoadingInvoices: Boolean = false, // Specific loading for invoices part
    val isGeneratingPdf: Boolean = false,
    val pdfGenerated: Boolean = false,
    val showAddTransactionDialog: Boolean = false,
    val showAddInvoiceDialog: Boolean = false, // For controlling AddInvoiceDialog visibility
    val error: String? = null,
    val invoiceError: String? = null // Specific error for invoice operations
)

// These data classes remain as they are related to FinancialReport, which is still part of FinanceViewModel
data class FinancialReport(
    val period: String,
    val totalIncome: Double,
    val totalExpenses: Double,
    val balance: Double,
    val transactionCount: Int,
    val topCategories: List<CategorySummary>,
    val monthlyTrend: List<MonthlyData>
)

data class CategorySummary(
    val category: String,
    val amount: Double,
    val percentage: Double
)

data class MonthlyData(
    val month: String,
    val income: Double,
    val expenses: Double
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val processTransactionUseCase: ProcessTransactionUseCase,
    private val generateFinancialReportUseCase: GenerateFinancialReportUseCase,
    private val getInvoicesUseCase: GetInvoicesUseCase,
    private val insertInvoiceUseCase: InsertInvoiceUseCase,
    private val generateInvoicePdfUseCase: GenerateInvoicePdfUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceUiState(isLoading = true, isLoadingInvoices = true))
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.update { it.copy(isLoading = true, isLoadingInvoices = true, error = null) }
                
                // Ejecutar operaciones en paralelo para mejor rendimiento
                val transactionsDeferred = async { loadTransactionsInternal() }
                val invoicesDeferred = async { loadInvoicesInternal() }
                
                // Esperar a que ambas operaciones terminen
                transactionsDeferred.await()
                invoicesDeferred.await()
                
                // Calcular resumen financiero en background
                calculateFinancialSummaryInternal()
                
                // Generar reportes en background sin bloquear
                launch(Dispatchers.IO) { 
                    generateReportsInternal() 
                }
                
                // Agregar datos de ejemplo si es necesario
                addSampleDataIfEmptyInternal()
                
                // Actualizar UI en el hilo principal
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            isLoadingInvoices = false,
                            error = "Error al cargar datos: ${e.message}"
                        ) 
                    }
                }
            }
        }
    }

    private suspend fun loadTransactionsInternal() {
        try {
            val incomeTransactions = processTransactionUseCase.getTransactionsByType(TransactionType.INCOME).first()
            val expenseTransactions = processTransactionUseCase.getTransactionsByType(TransactionType.EXPENSE).first()
            val allTransactions = (incomeTransactions + expenseTransactions).sortedByDescending { it.date }
            
            // Verificar que el ViewModel aún está activo antes de actualizar UI
            if (viewModelScope.isActive) {
                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            transactions = allTransactions,
                            filteredTransactions = if (it.searchQuery.isBlank()) allTransactions else filterTransactions(allTransactions, it.searchQuery)
                        )
                    }
                }
            }
        } catch (e: Exception) {
            if (viewModelScope.isActive) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(error = e.message ?: "Error cargando transacciones") }
                }
            }
        }
    }

    private suspend fun loadInvoicesInternal() {
        withContext(Dispatchers.Main) {
            _uiState.update { it.copy(isLoadingInvoices = true, invoiceError = null) }
        }
        try {
            val allInvoices = getInvoicesUseCase.getAllInvoices().first()
            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        invoices = allInvoices,
                        // filteredInvoices will be updated by applyInvoiceFilters
                        isLoadingInvoices = false
                    )
                }
            }
            applyInvoiceFilters() // Apply current filters after loading
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isLoadingInvoices = false, invoiceError = "Error al cargar facturas: ${e.message}") }
            }
        }
    }

    private fun applyInvoiceFilters() {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _uiState.value
            var currentInvoices = currentState.invoices

            // Apply status filter
            currentState.selectedInvoiceStatus?.let {
                status -> currentInvoices = currentInvoices.filter { it.status == status }
            }

            // Apply search query only if it's not blank
            if (currentState.invoiceSearchQuery.isNotBlank()) {
                currentInvoices = currentInvoices.filter { invoice ->
                    invoice.invoiceNumber.contains(currentState.invoiceSearchQuery, ignoreCase = true) ||
                    invoice.supplier.contains(currentState.invoiceSearchQuery, ignoreCase = true) ||
                    invoice.description?.contains(currentState.invoiceSearchQuery, ignoreCase = true) == true
                }
            }
            
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(filteredInvoices = currentInvoices) }
            }
        }
    }
    
    fun filterInvoicesByStatus(status: InvoiceStatus?){
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { it.copy(selectedInvoiceStatus = status) }
            applyInvoiceFilters()
        }
    }

    fun searchInvoices(query: String) {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { it.copy(invoiceSearchQuery = query) }
            applyInvoiceFilters()
        }
    }

    fun clearInvoiceSearchAndFilter() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { it.copy(invoiceSearchQuery = "", selectedInvoiceStatus = null) }
            applyInvoiceFilters()
        }
    }

    private suspend fun calculateFinancialSummaryInternal() {
        try {
            val totalIncome = processTransactionUseCase.getTotalByType(TransactionType.INCOME).first() ?: 0.0
            val totalExpenses = processTransactionUseCase.getTotalByType(TransactionType.EXPENSE).first() ?: 0.0
            val balance = totalIncome - totalExpenses
            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses,
                        balance = balance
                    )
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(error = e.message ?: "Error al calcular resumen") }
            }
        }
    }

    private suspend fun generateReportsInternal() {
        try {
            val report = generateFinancialReportUseCase()
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(financialReport = report) }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(error = "Error al generar reportes: ${e.message}") }
            }
        }
    }

    fun addTransaction(
        description: String,
        amount: Double,
        type: TransactionType,
        category: String,
        date: Date = Date()
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = processTransactionUseCase(
                    description = description,
                    amount = amount,
                    type = type,
                    category = category,
                    date = date
                )
                result.fold(
                    onSuccess = {
                        withContext(Dispatchers.Main) {
                            _uiState.update { it.copy(showAddTransactionDialog = false) }
                        }
                        loadTransactionsInternal()
                        calculateFinancialSummaryInternal()
                    },
                    onFailure = { exception ->
                        withContext(Dispatchers.Main) {
                            _uiState.update { it.copy(error = exception.message ?: "Error al agregar transacción") }
                        }
                    }
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(error = e.message ?: "Error inesperado") }
                }
            }
        }
    }

    fun showAddTransactionDialog() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { it.copy(showAddTransactionDialog = true) }
        }
    }

    fun hideAddTransactionDialog() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { it.copy(showAddTransactionDialog = false) }
        }
    }
    
    fun showAddInvoiceDialog() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { it.copy(showAddInvoiceDialog = true) }
        }
    }

    fun hideAddInvoiceDialog() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { it.copy(showAddInvoiceDialog = false) }
        }
    }

    fun clearError() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { it.copy(error = null, invoiceError = null) }
        }
    }

    fun refreshData() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isLoading = true, isLoadingInvoices = true, error = null, invoiceError = null) }
            }
            loadTransactionsInternal()
            loadInvoicesInternal()
            calculateFinancialSummaryInternal()
            generateReportsInternal()
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isLoading = false) } // General loading is for transactions part
            }
        }
    }

    // Helper function for filtering transactions to keep the main searchTransactions cleaner
    private fun filterTransactions(transactions: List<FinancialTransaction>, query: String): List<FinancialTransaction> {
        return transactions.filter { transaction ->
            transaction.description.contains(query, ignoreCase = true) ||
            transaction.category.contains(query, ignoreCase = true) ||
            transaction.amount.toString().contains(query, ignoreCase = true) ||
            transaction.reference?.contains(query, ignoreCase = true) == true ||
            transaction.notes?.contains(query, ignoreCase = true) == true
        }
    }

    fun searchTransactions(query: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val filtered = if (query.isBlank()) {
                _uiState.value.transactions
            } else {
                filterTransactions(_uiState.value.transactions, query)
            }
            
            withContext(Dispatchers.Main) {
                _uiState.update { 
                    it.copy(
                        searchQuery = query, 
                        isSearchActive = query.isNotBlank(),
                        filteredTransactions = filtered
                    ) 
                }
            }
        }
    }

    fun clearSearch() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update {
                it.copy(
                    searchQuery = "",
                    isSearchActive = false,
                    filteredTransactions = it.transactions
                )
            }
        }
    }

    fun addInvoice(
        invoiceNumber: String,
        supplier: String,
        amount: Double,
        taxAmount: Double,
        issueDate: Date,
        dueDate: Date,
        description: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isLoadingInvoices = true) }
            }
            try {
                val invoice = Invoice(
                    invoiceNumber = invoiceNumber,
                    supplier = supplier,
                    amount = amount,
                    taxAmount = taxAmount,
                    totalAmount = amount + taxAmount,
                    issueDate = issueDate,
                    dueDate = dueDate,
                    description = description
                )
                val result = insertInvoiceUseCase.insertInvoice(invoice)
                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(showAddInvoiceDialog = false) }
                    }
                    loadInvoicesInternal() // Reload invoices and re-apply filters
                } else {
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(invoiceError = "Error al agregar factura: ${result.exceptionOrNull()?.message}", isLoadingInvoices = false) }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(invoiceError = "Error al agregar factura: ${e.message}", isLoadingInvoices = false) }
                }
            }
        }
    }

    fun updateInvoiceStatus(invoiceId: Long, status: InvoiceStatus) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isLoadingInvoices = true) }
            }
            try {
                val result = insertInvoiceUseCase.updateInvoiceStatus(invoiceId, status)
                if (result.isSuccess) {
                    loadInvoicesInternal()
                } else {
                    withContext(Dispatchers.Main) {
                        _uiState.update { it.copy(invoiceError = "Error al actualizar estado: ${result.exceptionOrNull()?.message}", isLoadingInvoices = false) }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(invoiceError = "Error al actualizar estado: ${e.message}", isLoadingInvoices = false) }
                }
            }
        }
    }

    fun getInvoiceById(invoiceId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isLoadingInvoices = true) }
            }
            try {
                val invoice = getInvoicesUseCase.getInvoiceById(invoiceId).first()
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(selectedInvoice = invoice, isLoadingInvoices = false) }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(invoiceError = "Error al obtener factura: ${e.message}", isLoadingInvoices = false) }
                }
            }
        }
    }

    fun generateInvoicePdf(context: Context) {
        val invoice = _uiState.value.selectedInvoice
        if (invoice == null) {
            viewModelScope.launch(Dispatchers.Main) {
                _uiState.update { it.copy(invoiceError = "No hay factura seleccionada para generar PDF") }
            }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(isGeneratingPdf = true, invoiceError = null) }
            }
            try {
                val result = generateInvoicePdfUseCase.generateInvoicePdf(context, invoice)
                result.fold(
                    onSuccess = {
                        withContext(Dispatchers.Main) {
                            _uiState.update { oldState -> oldState.copy(isGeneratingPdf = false, pdfGenerated = true) }
                        }
                    },
                    onFailure = { exception ->
                        withContext(Dispatchers.Main) {
                            _uiState.update { oldState -> oldState.copy(isGeneratingPdf = false, invoiceError = "Error al generar PDF: ${exception.message}") }
                        }
                    }
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(isGeneratingPdf = false, invoiceError = "Error al generar PDF: ${e.message}") }
                }
            }
        }
    }

    fun clearPdfState() {
        viewModelScope.launch(Dispatchers.Main) {
            _uiState.update { it.copy(isGeneratingPdf = false, pdfGenerated = false, invoiceError = null) }
        }
    }
    
    fun reconcileSingleTransaction(transactionId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Marcar transacción como conciliada
                processTransactionUseCase.markTransactionAsReconciled(transactionId)
                
                // Recargar transacciones
                loadTransactionsInternal()
                calculateFinancialSummaryInternal()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(error = "Error al conciliar transacción: ${e.message}") }
                }
            }
        }
    }
    
    fun updateTransactionDetails(transactionId: Long, reference: String, notes: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Actualizar detalles de la transacción
                processTransactionUseCase.updateTransactionDetails(transactionId, reference, notes)
                
                // Recargar transacciones
                loadTransactionsInternal()
                calculateFinancialSummaryInternal()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(error = "Error al actualizar transacción: ${e.message}") }
                }
            }
        }
    }
    
    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Eliminar transacción
                processTransactionUseCase.deleteTransactionById(transactionId)
                
                // Recargar transacciones
                loadTransactionsInternal()
                calculateFinancialSummaryInternal()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(error = "Error al eliminar transacción: ${e.message}") }
                }
            }
        }
    }

    fun addTestTransaction() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val testTransaction = FinancialTransaction(
                    description = "Transacción de prueba ${System.currentTimeMillis()}",
                    amount = (100..1000).random().toDouble(),
                    type = if (Math.random() > 0.5) TransactionType.INCOME else TransactionType.EXPENSE,
                    category = listOf("Prueba", "Test", "Demo").random(),
                    date = Date()
                )
                processTransactionUseCase(
                    description = testTransaction.description,
                    amount = testTransaction.amount,
                    type = testTransaction.type,
                    category = testTransaction.category,
                    date = testTransaction.date
                )
                loadTransactionsInternal()
                calculateFinancialSummaryInternal()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(error = "Error al agregar transacción de prueba: ${e.message}") }
                }
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = processTransactionUseCase.deleteAllTransactions()
                // Consider deleting invoices as well if "all data" means everything
                // insertInvoiceUseCase.deleteAllInvoices() // Example, if such a method exists
                result.fold(
                    onSuccess = {
                        withContext(Dispatchers.Main) {
                            _uiState.update { oldState ->
                                oldState.copy(
                                    transactions = emptyList(),
                                    filteredTransactions = emptyList(),
                                    totalIncome = 0.0,
                                    totalExpenses = 0.0,
                                    balance = 0.0,
                                    invoices = emptyList(),
                                    filteredInvoices = emptyList(),
                                    financialReport = null,
                                    selectedInvoiceStatus = null, // Reset filters
                                    invoiceSearchQuery = "" // Reset search
                                )
                            }
                        }
                        addSampleDataIfEmptyInternal()
                    },
                    onFailure = { exception ->
                        withContext(Dispatchers.Main) {
                            _uiState.update { it.copy(error = "Error al limpiar datos: ${exception.message}") }
                        }
                    }
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _uiState.update { it.copy(error = "Error al limpiar datos: ${e.message}") }
                }
            }
        }
    }

    private suspend fun addSampleDataIfEmptyInternal() {
        try {
            if (_uiState.value.transactions.isEmpty()) {
                val sampleTransactions = listOf(
                    FinancialTransaction(description = "Venta de proyecto web", amount = 5000.0, type = TransactionType.INCOME, category = "Ventas", date = Date()),
                    FinancialTransaction(description = "Pago de nómina", amount = 3200.0, type = TransactionType.EXPENSE, category = "Personal", date = Date())
                )
                sampleTransactions.forEach { transaction ->
                    processTransactionUseCase(
                        description = transaction.description, amount = transaction.amount, type = transaction.type,
                        category = transaction.category, date = transaction.date
                    )
                }
                loadTransactionsInternal()
                calculateFinancialSummaryInternal()
            }
            if (_uiState.value.invoices.isEmpty()) {
                addSampleInvoicesInternal()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(error = "Error al cargar datos de prueba: ${e.message}") }
            }
        }
    }

    private suspend fun addSampleInvoicesInternal() {
        try {
            val sampleInvoices = listOf(
                Invoice(invoiceNumber = "INV-001", supplier = "Proveedor A", amount = 500000.0, taxAmount = 95000.0, totalAmount = 595000.0, issueDate = Date(), dueDate = Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L), status = InvoiceStatus.PENDING),
                Invoice(invoiceNumber = "INV-002", supplier = "Proveedor B", amount = 750000.0, taxAmount = 142500.0, totalAmount = 892500.0, issueDate = Date(), dueDate = Date(System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000L), status = InvoiceStatus.APPROVED)
            )
            sampleInvoices.forEach { invoice ->
                insertInvoiceUseCase.insertInvoice(invoice)
            }
            loadInvoicesInternal()
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                _uiState.update { it.copy(invoiceError = "Error al agregar facturas de prueba: ${e.message}") }
            }
        }
    }
}
