package com.solarcalculator.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.solarcalculator.data.model.CalculationResult
import com.solarcalculator.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryAdapter(
    private val onItemClick: (CalculationResult) -> Unit,
    private val onDeleteClick: (CalculationResult) -> Unit
) : ListAdapter<CalculationResult, HistoryAdapter.ViewHolder>(DiffCallback()) {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    inner class ViewHolder(
        private val binding: ItemHistoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(calculation: CalculationResult) {
            binding.apply {
                calculationNameText.text = calculation.calculationName
                calculationDateText.text = dateFormat.format(calculation.createdAt)
                capacityText.text = String.format("%.1fkW", calculation.installedCapacity)
                cityText.text = calculation.cityName
                annualGenerationText.text = String.format("%.0fkWh/年", calculation.annualGeneration)
                annualRevenueText.text = String.format("%.0f元/年", calculation.annualRevenue)
                paybackPeriodText.text = String.format("%.1f年回本", calculation.paybackPeriod)
                
                root.setOnClickListener { onItemClick(calculation) }
                deleteButton.setOnClickListener { onDeleteClick(calculation) }
            }
        }
    }
    
    class DiffCallback : DiffUtil.ItemCallback<CalculationResult>() {
        override fun areItemsTheSame(oldItem: CalculationResult, newItem: CalculationResult): Boolean {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(oldItem: CalculationResult, newItem: CalculationResult): Boolean {
            return oldItem == newItem
        }
    }
}
