package com.solarcalculator.data.repository

import com.solarcalculator.data.local.CalculationHistoryDao
import com.solarcalculator.data.local.CitySolarDataDao
import com.solarcalculator.data.model.CalculationInput
import com.solarcalculator.data.model.CalculationResult
import com.solarcalculator.data.model.CitySolarData
import com.solarcalculator.utils.SolarCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SolarCalculatorRepository @Inject constructor(
    private val calculationDao: CalculationHistoryDao,
    private val cityDao: CitySolarDataDao
) {
    
    // 城市数据操作
    fun getAllCities() = cityDao.getAllCities()
    
    suspend fun getCityByName(cityName: String): CitySolarData? {
        return cityDao.getCityByName(cityName)
    }
    
    suspend fun searchCities(query: String): List<CitySolarData> {
        return cityDao.searchCities(query)
    }
    
    suspend fun getCitiesByProvince(province: String): List<CitySolarData> {
        return cityDao.getCitiesByProvince(province)
    }
    
    // 计算历史操作
    fun getAllCalculations() = calculationDao.getAllCalculations()
    
    suspend fun getCalculationById(id: Long): CalculationResult? {
        return calculationDao.getCalculationById(id)
    }
    
    suspend fun saveCalculation(calculation: CalculationResult): Long {
        return calculationDao.insertCalculation(calculation)
    }
    
    suspend fun deleteCalculation(calculation: CalculationResult) {
        calculationDao.deleteCalculation(calculation)
    }
    
    suspend fun deleteCalculationById(id: Long) {
        calculationDao.deleteCalculationById(id)
    }
    
    suspend fun deleteAllCalculations() {
        calculationDao.deleteAllCalculations()
    }
    
    // 核心计算功能
    fun calculateSolarData(input: CalculationInput): Flow<Result<CalculationResult>> = flow {
        try {
            val cityData = cityDao.getCityByName(input.cityName)
                ?: throw IllegalArgumentException("未找到城市: ${input.cityName}")
            
            val result = SolarCalculator.calculate(input, cityData)
            emit(Result.success(result))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }.flowOn(Dispatchers.Default)
    
    // 快速计算（不保存）
    suspend fun quickCalculate(input: CalculationInput): CalculationResult {
        val cityData = cityDao.getCityByName(input.cityName)
            ?: throw IllegalArgumentException("未找到城市: ${input.cityName}")
        return SolarCalculator.calculate(input, cityData)
    }
    
    // 计算并保存
    suspend fun calculateAndSave(
        input: CalculationInput,
        calculationName: String = ""
    ): CalculationResult {
        val cityData = cityDao.getCityByName(input.cityName)
            ?: throw IllegalArgumentException("未找到城市: ${input.cityName}")
        
        val result = SolarCalculator.calculate(input, cityData).copy(
            calculationName = calculationName.ifEmpty { 
                "${input.cityName} ${input.installedCapacity}kW ${input.panelType.displayName}" 
            }
        )
        
        calculationDao.insertCalculation(result)
        return result
    }
}
