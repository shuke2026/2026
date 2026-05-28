package com.solarcalculator.di

import com.solarcalculator.data.repository.SolarCalculatorRepository
import com.solarcalculator.data.local.CalculationHistoryDao
import com.solarcalculator.data.local.CitySolarDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideSolarCalculatorRepository(
        calculationDao: CalculationHistoryDao,
        cityDao: CitySolarDataDao
    ): SolarCalculatorRepository {
        return SolarCalculatorRepository(calculationDao, cityDao)
    }
}
