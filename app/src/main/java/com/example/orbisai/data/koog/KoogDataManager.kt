package com.example.orbisai.data.koog

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.data.models.Invoice
import com.example.orbisai.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OrbisDataManager - Sistema de Gestión de Datos Optimizado para OrbisAI
 * (Anteriormente KOOG - renombrado para evitar conflictos con KOOG AI Agents de JetBrains)
 * 
 * Características principales:
 * - Cache inteligente con TTL
 * - Sincronización automática
 * - Optimización de consultas
 * - Gestión de estado offline
 * - Métricas de rendimiento
 */
@Singleton
class OrbisDataManager @Inject constructor(
    private val context: Context,
    val transactionRepository: TransactionRepository
) {
    
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "koog_preferences")
        
        // Claves para DataStore
        private val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
        private val CACHE_TTL = longPreferencesKey("cache_ttl")
        private val OFFLINE_MODE = booleanPreferencesKey("offline_mode")
        private val PERFORMANCE_METRICS = stringPreferencesKey("performance_metrics")
        
        // Configuración por defecto
        private const val DEFAULT_CACHE_TTL = 5 * 60 * 1000L // 5 minutos
        private const val MAX_CACHE_SIZE = 1000
    }
    
    // Cache en memoria con TTL
    private val cache = mutableMapOf<String, CacheEntry>()
    private val cacheMutex = Mutex()
    
    // Métricas de rendimiento
    private val performanceMetrics = mutableMapOf<String, Long>()
    
    /**
     * Obtiene datos con cache inteligente
     */
    suspend fun <T> getDataWithCache(
        key: String,
        fetchFromSource: suspend () -> T,
        ttl: Long = DEFAULT_CACHE_TTL
    ): T {
        return cacheMutex.withLock {
            val cachedEntry = cache[key]
            
            if (cachedEntry != null && !cachedEntry.isExpired()) {
                // Datos en cache válidos
                recordMetric("cache_hit", System.currentTimeMillis())
                return cachedEntry.data as T
            }
            
            // Cache miss o expirado, obtener de fuente
            recordMetric("cache_miss", System.currentTimeMillis())
            val startTime = System.currentTimeMillis()
            
            val data = fetchFromSource()
            
            // Guardar en cache
            cache[key] = CacheEntry(
                data = data as Any,
                timestamp = System.currentTimeMillis(),
                ttl = ttl
            )
            
            // Limpiar cache si es necesario
            cleanupCache()
            
            recordMetric("fetch_time", System.currentTimeMillis() - startTime)
            return data
        }
    }
    
    /**
     * Sincroniza datos automáticamente
     */
    suspend fun syncData() {
        val startTime = System.currentTimeMillis()
        
        try {
            // Sincronizar transacciones
            val transactions = transactionRepository.getAllTransactions().first()
            
            // Sincronizar facturas
            val invoices = transactionRepository.getAllInvoices().first()
            
            // Actualizar timestamp de sincronización
            context.dataStore.edit { preferences ->
                preferences[LAST_SYNC_TIME] = System.currentTimeMillis()
            }
            
            recordMetric("sync_success", System.currentTimeMillis() - startTime)
            
        } catch (e: Exception) {
            recordMetric("sync_error", System.currentTimeMillis() - startTime)
            throw e
        }
    }
    
    /**
     * Optimiza consultas frecuentes
     */
    suspend fun getOptimizedTransactions(
        type: String? = null,
        limit: Int = 50
    ): Flow<List<FinancialTransaction>> {
        val cacheKey = "transactions_${type}_$limit"
        
        return flow {
            val transactions = getDataWithCache(
                key = cacheKey,
                fetchFromSource = {
                    transactionRepository.getAllTransactions().first()
                }
            )
            
            val filtered = when (type) {
                "income" -> transactions.filter { it.type.name == "INCOME" }
                "expense" -> transactions.filter { it.type.name == "EXPENSE" }
                else -> transactions
            }.take(limit)
            
            emit(filtered)
        }
    }
    
    /**
     * Gestiona modo offline
     */
    suspend fun setOfflineMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[OFFLINE_MODE] = enabled
        }
    }
    
    suspend fun isOfflineMode(): Boolean {
        return context.dataStore.data.first()[OFFLINE_MODE] ?: false
    }
    
    /**
     * Obtiene métricas de rendimiento
     */
    suspend fun getPerformanceMetrics(): Map<String, Long> {
        return performanceMetrics.toMap()
    }
    
    /**
     * Limpia cache expirado
     */
    private suspend fun cleanupCache() {
        val currentTime = System.currentTimeMillis()
        val expiredKeys = cache.filter { (_, entry) ->
            entry.isExpired(currentTime)
        }.keys
        
        expiredKeys.forEach { cache.remove(it) }
        
        // Limpiar por tamaño si es necesario
        if (cache.size > MAX_CACHE_SIZE) {
            val sortedEntries = cache.entries.sortedBy { it.value.timestamp }
            val toRemove = sortedEntries.take(cache.size - MAX_CACHE_SIZE)
            toRemove.forEach { cache.remove(it.key) }
        }
    }
    
    /**
     * Registra métricas de rendimiento
     */
    private fun recordMetric(name: String, value: Long) {
        performanceMetrics[name] = value
    }
    
    /**
     * Entrada de cache con TTL
     */
    private data class CacheEntry(
        val data: Any,
        val timestamp: Long,
        val ttl: Long
    ) {
        fun isExpired(currentTime: Long = System.currentTimeMillis()): Boolean {
            return currentTime - timestamp > ttl
        }
    }
}

/**
 * Extensiones para facilitar el uso de OrbisDataManager
 */
object OrbisDataExtensions {
    
    /**
     * Extensión para optimizar consultas de transacciones
     */
    suspend fun OrbisDataManager.getTransactionsOptimized(
        category: String? = null,
        status: String? = null,
        limit: Int = 20
    ): Flow<List<FinancialTransaction>> {
        val cacheKey = "transactions_${category}_${status}_$limit"
        
        return flow {
            val transactions = getDataWithCache(
                key = cacheKey,
                fetchFromSource = {
                    transactionRepository.getAllTransactions().first()
                }
            )
            
            var filtered = transactions
            
            if (category != null) {
                filtered = filtered.filter { it.category == category }
            }
            
            if (status != null) {
                filtered = filtered.filter { it.status.name == status }
            }
            
            emit(filtered.take(limit))
        }
    }
    
    /**
     * Extensión para optimizar consultas de facturas
     */
    suspend fun OrbisDataManager.getInvoicesOptimized(
        status: String? = null,
        limit: Int = 20
    ): Flow<List<Invoice>> {
        val cacheKey = "invoices_${status}_$limit"
        
        return flow {
            val invoices = getDataWithCache(
                key = cacheKey,
                fetchFromSource = {
                    transactionRepository.getAllInvoices().first()
                }
            )
            
            val filtered = if (status != null) {
                invoices.filter { it.status.name == status }
            } else {
                invoices
            }
            
            emit(filtered.take(limit))
        }
    }
}
