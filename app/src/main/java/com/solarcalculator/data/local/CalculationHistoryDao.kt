package com.solarcalculator.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.solarcalculator.data.model.CalculationResult

@Dao
interface CalculationHistoryDao {
    
    @Query("SELECT * FROM calculation_history ORDER BY createdAt DESC")
    fun getAllCalculations(): LiveData<List<CalculationResult>>
    
    @Query("SELECT * FROM calculation_history ORDER BY createdAt DESC")
    suspend fun getAllCalculationsSync(): List<CalculationResult>
    
    @Query("SELECT * FROM calculation_history WHERE id = :id")
    suspend fun getCalculationById(id: Long): CalculationResult?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalculation(calculation: CalculationResult): Long
    
    @Delete
    suspend fun deleteCalculation(calculation: CalculationResult)
    
    @Query("DELETE FROM calculation_history WHERE id = :id")
    suspend fun deleteCalculationById(id: Long)
    
    @Query("DELETE FROM calculation_history")
    suspend fun deleteAllCalculations()
    
    @Query("SELECT COUNT(*) FROM calculation_history")
    suspend fun getCalculationCount(): Int
    
    @Query("SELECT * FROM calculation_history ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getRecentCalculations(limit: Int): List<CalculationResult>
}
