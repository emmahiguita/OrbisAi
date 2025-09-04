package com.example.orbisai.data.local

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.example.orbisai.data.local.converters.DateConverter
import com.example.orbisai.data.local.converters.EnumConverters
import com.example.orbisai.data.local.dao.TransactionDao
import com.example.orbisai.data.local.dao.InvoiceDao
import com.example.orbisai.data.local.dao.EmployeeDao
import com.example.orbisai.data.local.dao.SaleDao
import com.example.orbisai.data.models.FinancialTransaction
import com.example.orbisai.data.models.Invoice
import com.example.orbisai.data.models.Employee
import com.example.orbisai.data.models.Sale

@Database(
    entities = [
        FinancialTransaction::class,
        Invoice::class,
        Employee::class,
        Sale::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, EnumConverters::class)
abstract class OrbisDatabase : RoomDatabase() {
    
    abstract fun transactionDao(): TransactionDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun employeeDao(): EmployeeDao
    abstract fun saleDao(): SaleDao
    
    companion object {
        @Volatile
        private var INSTANCE: OrbisDatabase? = null
        
        fun getDatabase(context: Context): OrbisDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OrbisDatabase::class.java,
                    "orbis_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
