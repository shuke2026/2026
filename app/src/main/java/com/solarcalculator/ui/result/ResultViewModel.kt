package com.solarcalculator.ui.result

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
class ResultViewModel @Inject constructor(
    private val repository: SolarCalculatorRepository
) : ViewModel() {
    
    private val _calculationResult = MutableLiveData<CalculationResult?>(null)
    val calculationResult: LiveData<CalculationResult?> = _calculationResult
    
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    
    fun loadCalculation(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.getCalculationById(id)
                _calculationResult.value = result
            } catch (e: Exception) {
                _error.value = e.message ?: "加载失败"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun loadLatestCalculation() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val calculations = repository.getAllCalculations().value
                _calculationResult.value = calculations?.firstOrNull()
            } catch (e: Exception) {
                _error.value = e.message ?: "加载失败"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun saveCalculation(name: String) {
        val result = _calculationResult.value ?: return
        
        viewModelScope.launch {
            try {
                val updatedResult = result.copy(calculationName = name)
                repository.saveCalculation(updatedResult)
                _calculationResult.value = updatedResult
            } catch (e: Exception) {
                _error.value = e.message ?: "保存失败"
            }
        }
    }
}
