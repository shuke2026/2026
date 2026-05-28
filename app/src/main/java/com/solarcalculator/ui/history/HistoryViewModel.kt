package com.solarcalculator.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solarcalculator.data.model.CalculationResult
import com.solarcalculator.data.repository.SolarCalculatorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: SolarCalculatorRepository
) : ViewModel() {
    
    val allCalculations = repository.getAllCalculations()
    
    fun deleteCalculation(calculation: CalculationResult) {
        viewModelScope.launch {
            repository.deleteCalculation(calculation)
        }
    }
    
    fun deleteAllCalculations() {
        viewModelScope.launch {
            repository.deleteAllCalculations()
        }
    }
}
