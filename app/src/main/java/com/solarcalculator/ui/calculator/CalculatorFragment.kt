package com.solarcalculator.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.slider.Slider
import com.google.android.material.snackbar.Snackbar
import com.solarcalculator.R
import com.solarcalculator.data.model.Orientation
import com.solarcalculator.data.model.SolarPanelType
import com.solarcalculator.databinding.FragmentCalculatorBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalculatorFragment : Fragment() {
    
    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CalculatorViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalculatorBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupSpinners()
        setupSliders()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupSpinners() {
        // 城市选择
        viewModel.cities.observe(viewLifecycleOwner) { cities ->
            val cityNames = cities.map { "${it.cityName} (${it.province})" }
            val cityAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                cityNames
            )
            binding.cityAutoComplete.setAdapter(cityAdapter)
        }
        
        // 组件类型
        val panelTypes = SolarPanelType.values().map { it.displayName }
        val panelAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            panelTypes
        )
        panelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.panelTypeSpinner.adapter = panelAdapter
        
        // 朝向选择
        val orientations = Orientation.values().map { it.displayName }
        val orientationAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            orientations
        )
        orientationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.orientationSpinner.adapter = orientationAdapter
    }
    
    private fun setupSliders() {
        // 装机容量滑块
        binding.capacitySlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            binding.capacityValueText.text = String.format("%.1f kW", value)
            viewModel.updateInput { it.copy(installedCapacity = value.toDouble()) }
        })
        
        // 安装角度滑块
        binding.tiltAngleSlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            binding.tiltAngleValueText.text = String.format("%.0f°", value)
            viewModel.updateInput { it.copy(tiltAngle = value.toDouble()) }
        })
        
        // 系统成本滑块
        binding.costSlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            binding.costValueText.text = String.format("%.0f 元", value)
            viewModel.updateInput { it.copy(systemCost = value.toDouble()) }
        })
        
        // 电价滑块
        binding.electricityPriceSlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            binding.electricityPriceValueText.text = String.format("%.2f 元/kWh", value)
            viewModel.updateInput { it.copy(electricityPrice = value.toDouble()) }
        })
        
        // 上网电价滑块
        binding.feedInTariffSlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            binding.feedInTariffValueText.text = String.format("%.2f 元/kWh", value)
            viewModel.updateInput { it.copy(feedInTariff = value.toDouble()) }
        })
        
        // 自用电比例滑块
        binding.selfConsumptionSlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            binding.selfConsumptionValueText.text = String.format("%.0f%%", value * 100)
            viewModel.updateInput { it.copy(selfConsumptionRatio = value.toDouble()) }
        })
    }
    
    private fun setupButtons() {
        binding.calculateButton.setOnClickListener {
            collectInputData()
            viewModel.calculate()
        }
        
        binding.resetButton.setOnClickListener {
            viewModel.reset()
            resetUI()
        }
    }
    
    private fun collectInputData() {
        val cityText = binding.cityAutoComplete.text.toString()
        val cityName = cityText.substringBefore(" (")
        
        val panelType = SolarPanelType.values()[binding.panelTypeSpinner.selectedItemPosition]
        val orientation = Orientation.values()[binding.orientationSpinner.selectedItemPosition]
        
        viewModel.updateInput {
            it.copy(
                cityName = cityName,
                panelType = panelType,
                orientation = orientation
            )
        }
    }
    
    private fun observeViewModel() {
        viewModel.calculationInput.observe(viewLifecycleOwner) { input ->
            // 更新UI以反映当前输入值
            binding.capacitySlider.value = input.installedCapacity.toFloat()
            binding.tiltAngleSlider.value = input.tiltAngle.toFloat()
            binding.costSlider.value = input.systemCost.toFloat()
            binding.electricityPriceSlider.value = input.electricityPrice.toFloat()
            binding.feedInTariffSlider.value = input.feedInTariff.toFloat()
            binding.selfConsumptionSlider.value = input.selfConsumptionRatio.toFloat()
        }
        
        viewModel.calculationResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                navigateToResult()
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.calculateButton.isEnabled = !isLoading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        
        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }
    }
    
    private fun navigateToResult() {
        val action = CalculatorFragmentDirections.actionCalculatorToResult(-1)
        findNavController().navigate(action)
    }
    
    private fun resetUI() {
        binding.cityAutoComplete.setText("")
        binding.panelTypeSpinner.setSelection(0)
        binding.orientationSpinner.setSelection(0)
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
