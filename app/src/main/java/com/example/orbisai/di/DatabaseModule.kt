package com.example.orbisai.di

import android.content.Context
import androidx.room.Room
import com.example.orbisai.data.local.OrbisDatabase
import com.example.orbisai.data.local.dao.TransactionDao
import com.example.orbisai.data.local.dao.InvoiceDao
import com.example.orbisai.data.local.dao.EmployeeDao
import com.example.orbisai.data.local.dao.SaleDao
import com.example.orbisai.data.koog.OrbisDataManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OrbisDatabase {
        return Room.databaseBuilder(
            context,
            OrbisDatabase::class.java,
            "orbis_database"
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    fun provideTransactionDao(database: OrbisDatabase): TransactionDao {
        return database.transactionDao()
    }
    
    @Provides
    fun provideInvoiceDao(database: OrbisDatabase): InvoiceDao {
        return database.invoiceDao()
    }
    
    @Provides
    fun provideEmployeeDao(database: OrbisDatabase): EmployeeDao {
        return database.employeeDao()
    }
    
    @Provides
    fun provideSaleDao(database: OrbisDatabase): SaleDao {
        return database.saleDao()
    }
    
    @Provides
    @Singleton
    fun provideOrbisDataManager(
        @ApplicationContext context: Context,
        transactionRepository: com.example.orbisai.data.repository.TransactionRepository
    ): OrbisDataManager {
        return OrbisDataManager(context, transactionRepository)
    }
}
