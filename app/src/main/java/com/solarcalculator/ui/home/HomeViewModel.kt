package com.solarcalculator.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solarcalculator.data.model.CalculationResult
import com.solarcalculator.data.repository.SolarCalculatorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SolarCalculatorRepository
) : ViewModel() {
    
    val recentCalculations: LiveData<List<CalculationResult>> = repository.getAllCalculations()
    
    private val _statistics = MutableLiveData<HomeStatistics>()
    val statistics: LiveData<HomeStatistics> = _statistics
    
    init {
        loadStatistics()
    }
    
    private fun loadStatistics() {
        viewModelScope.launch {
            val calculations = repository.getAllCalculations().value ?: emptyList()
            
            if (calculations.isNotEmpty()) {
                val totalCapacity = calculations.sumOf { it.installedCapacity }
                val totalGeneration = calculations.sumOf { it.annualGeneration }
                val avgPaybackPeriod = calculations.map { it.paybackPeriod }.average()
                
                _statistics.value = HomeStatistics(
                    totalCalculations = calculations.size,
                    totalCapacity = totalCapacity,
                    totalAnnualGeneration = totalGeneration,
                    averagePaybackPeriod = avgPaybackPeriod
                )
            }
        }
    }
    
    fun deleteCalculation(calculation: CalculationResult) {
        viewModelScope.launch {
            repository.deleteCalculation(calculation)
        }
    }
    
    data class HomeStatistics(
        val totalCalculations: Int,
        val totalCapacity: Double,
        val totalAnnualGeneration: Double,
        val averagePaybackPeriod: Double
    )
}
