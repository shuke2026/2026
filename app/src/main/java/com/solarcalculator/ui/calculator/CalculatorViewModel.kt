package com.solarcalculator.ui.calculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solarcalculator.data.model.CalculationInput
import com.solarcalculator.data.model.CalculationResult
import com.solarcalculator.data.model.CitySolarData
import com.solarcalculator.data.repository.SolarCalculatorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val repository: SolarCalculatorRepository
) : ViewModel() {
    
    private val _calculationInput = MutableLiveData(CalculationInput.default())
    val calculationInput: LiveData<CalculationInput> = _calculationInput
    
    private val _calculationResult = MutableLiveData<CalculationResult?>(null)
    val calculationResult: LiveData<CalculationResult?> = _calculationResult
    
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    
    private val _cities = MutableLiveData<List<CitySolarData>>()
    val cities: LiveData<List<CitySolarData>> = _cities
    
    init {
        loadCities()
    }
    
    private fun loadCities() {
        viewModelScope.launch {
            repository.getAllCities().observeForever { cityList ->
                _cities.value = cityList
            }
        }
    }
    
    fun updateInput(update: (CalculationInput) -> CalculationInput) {
        _calculationInput.value = update(_calculationInput.value ?: CalculationInput.default())
    }
    
    fun calculate() {
        val input = _calculationInput.value ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                repository.calculateSolarData(input).collectLatest { result ->
                    result.onSuccess { calculationResult ->
                        _calculationResult.value = calculationResult
                    }.onFailure { exception ->
                        _error.value = exception.message ?: "计算失败"
                    }
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "计算失败"
                _isLoading.value = false
            }
        }
    }
    
    fun saveCalculation(name: String = "") {
        val result = _calculationResult.value ?: return
        
        viewModelScope.launch {
            try {
                val savedResult = result.copy(calculationName = name.ifEmpty { result.calculationName })
                repository.saveCalculation(savedResult)
            } catch (e: Exception) {
                _error.value = e.message ?: "保存失败"
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun reset() {
        _calculationInput.value = CalculationInput.default()
        _calculationResult.value = null
        _error.value = null
    }
}
