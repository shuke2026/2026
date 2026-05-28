package com.solarcalculator.di

import android.content.Context
import com.solarcalculator.data.local.AppDatabase
import com.solarcalculator.data.local.CalculationHistoryDao
import com.solarcalculator.data.local.CitySolarDataDao
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }
    
    @Provides
    fun provideCalculationHistoryDao(database: AppDatabase): CalculationHistoryDao {
        return database.calculationHistoryDao()
    }
    
    @Provides
    fun provideCitySolarDataDao(database: AppDatabase): CitySolarDataDao {
        return database.citySolarDataDao()
    }
}
