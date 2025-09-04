package com.example.orbisai.di

import com.example.orbisai.ai.OrbisAIAgent
import com.example.orbisai.data.repository.TransactionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AIModule {
    
    @Provides
    @Singleton
    fun provideOrbisAIAgent(
        transactionRepository: TransactionRepository
    ): OrbisAIAgent {
        return OrbisAIAgent(transactionRepository)
    }
}
