package com.example.orbisai.di

import com.example.orbisai.data.repository.TransactionRepository
import com.example.orbisai.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideProcessTransactionUseCase(
        repository: TransactionRepository
    ): ProcessTransactionUseCase {
        return ProcessTransactionUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGenerateFinancialReportUseCase(
        repository: TransactionRepository
    ): GenerateFinancialReportUseCase {
        return GenerateFinancialReportUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideReconcileTransactionsUseCase(
        repository: TransactionRepository
    ): ReconcileTransactionsUseCase {
        return ReconcileTransactionsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetInvoicesUseCase(
        repository: TransactionRepository
    ): GetInvoicesUseCase {
        return GetInvoicesUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideInsertInvoiceUseCase(
        repository: TransactionRepository
    ): InsertInvoiceUseCase {
        return InsertInvoiceUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGenerateInvoicePdfUseCase(
        repository: TransactionRepository
    ): GenerateInvoicePdfUseCase {
        return GenerateInvoicePdfUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideExportReconciliationDataUseCase(
        repository: TransactionRepository
    ): ExportReconciliationDataUseCase {
        return ExportReconciliationDataUseCase(repository)
    }
}
