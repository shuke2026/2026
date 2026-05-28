package com.solarcalculator.ui.result

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.solarcalculator.R
import com.solarcalculator.databinding.FragmentResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultFragment : Fragment() {
    
    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ResultViewModel by viewModels()
    private val args: ResultFragmentArgs by navArgs()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupButtons()
        observeViewModel()
        
        // 加载计算结果
        if (args.calculationId > 0) {
            viewModel.loadCalculation(args.calculationId)
        } else {
            viewModel.loadLatestCalculation()
        }
    }
    
    private fun setupButtons() {
        binding.saveButton.setOnClickListener {
            showSaveDialog()
        }
        
        binding.shareButton.setOnClickListener {
            shareResult()
        }
        
        binding.newCalculationButton.setOnClickListener {
            findNavController().navigate(R.id.action_result_to_calculator)
        }
        
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun observeViewModel() {
        viewModel.calculationResult.observe(viewLifecycleOwner) { result ->
            result?.let {
                displayResults(it)
                setupCharts(it)
            }
        }
        
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
    
    private fun displayResults(result: com.solarcalculator.data.model.CalculationResult) {
        binding.apply {
            // 基本信息
            calculationTitleText.text = result.calculationName
            capacityText.text = String.format("%.1f kW", result.installedCapacity)
            cityText.text = result.cityName
            panelTypeText.text = result.panelType
            
            // 发电量
            annualGenerationText.text = String.format("%.0f kWh", result.annualGeneration)
            dailyGenerationText.text = String.format("%.1f kWh", result.dailyAverageGeneration)
            systemEfficiencyText.text = String.format("%.1f%%", result.systemEfficiency * 100)
            
            // 收益
            annualRevenueText.text = String.format("%.0f 元", result.annualRevenue)
            totalRevenueText.text = String.format("%.0f 元", result.cumulativeRevenue.lastOrNull() ?: 0.0)
            npvText.text = String.format("%.0f 元", result.netPresentValue)
            
            // 回本分析
            paybackPeriodText.text = String.format("%.1f 年", result.paybackPeriod)
            irrText.text = String.format("%.1f%%", result.internalRateOfReturn * 100)
        }
    }
    
    private fun setupCharts(result: com.solarcalculator.data.model.CalculationResult) {
        setupMonthlyGenerationChart(result)
        setupCumulativeRevenueChart(result)
    }
    
    private fun setupMonthlyGenerationChart(result: com.solarcalculator.data.model.CalculationResult) {
        val entries = result.monthlyGeneration.mapIndexed { index, value ->
            BarEntry(index.toFloat(), value.toFloat())
        }
        
        val dataSet = BarDataSet(entries, "月发电量 (kWh)").apply {
            color = requireContext().getColor(R.color.primary_green)
            valueTextSize = 10f
        }
        
        binding.monthlyGenerationChart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(
                    listOf("1月", "2月", "3月", "4月", "5月", "6月", 
                           "7月", "8月", "9月", "10月", "11月", "12月")
                )
                granularity = 1f
            }
            
            axisLeft.apply {
                axisMinimum = 0f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return String.format("%.0f", value)
                    }
                }
            }
            axisRight.isEnabled = false
            
            invalidate()
        }
    }
    
    private fun setupCumulativeRevenueChart(result: com.solarcalculator.data.model.CalculationResult) {
        val entries = result.cumulativeRevenue.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }
        
        val dataSet = LineDataSet(entries, "累计收益 (元)").apply {
            color = requireContext().getColor(R.color.primary_blue)
            setCircleColor(color)
            lineWidth = 2f
            circleRadius = 3f
            valueTextSize = 10f
        }
        
        binding.cumulativeRevenueChart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = true
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return "${value.toInt()}年"
                    }
                }
                granularity = 5f
            }
            
            axisLeft.apply {
                axisMinimum = 0f
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return String.format("%.0f", value)
                    }
                }
            }
            axisRight.isEnabled = false
            
            invalidate()
        }
    }
    
    private fun showSaveDialog() {
        val editText = android.widget.EditText(requireContext()).apply {
            hint = "输入测算名称"
        }
        
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("保存测算")
            .setView(editText)
            .setPositiveButton("保存") { _, _ ->
                val name = editText.text.toString()
                viewModel.saveCalculation(name)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun shareResult() {
        val result = viewModel.calculationResult.value ?: return
        
        val shareText = buildString {
            appendLine("🌞 光伏发电测算结果")
            appendLine()
            appendLine("【系统配置】")
            appendLine("装机容量: ${String.format("%.1f", result.installedCapacity)} kW")
            appendLine("安装城市: ${result.cityName}")
            appendLine("组件类型: ${result.panelType}")
            appendLine()
            appendLine("【发电量】")
            appendLine("年发电量: ${String.format("%.0f", result.annualGeneration)} kWh")
            appendLine("日均发电: ${String.format("%.1f", result.dailyAverageGeneration)} kWh")
            appendLine()
            appendLine("【收益分析】")
            appendLine("年收益: ${String.format("%.0f", result.annualRevenue)} 元")
            appendLine("投资回收期: ${String.format("%.1f", result.paybackPeriod)} 年")
            appendLine("净现值: ${String.format("%.0f", result.netPresentValue)} 元")
        }
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "光伏发电测算结果")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        
        startActivity(Intent.createChooser(intent, "分享测算结果"))
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
