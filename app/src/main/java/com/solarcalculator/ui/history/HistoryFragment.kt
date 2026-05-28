package com.solarcalculator.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.solarcalculator.data.model.CalculationResult
import com.solarcalculator.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment() {
    
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(
            onItemClick = { calculation ->
                navigateToResult(calculation)
            },
            onDeleteClick = { calculation ->
                showDeleteConfirmation(calculation)
            }
        )
        
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }
    
    private fun setupButtons() {
        binding.deleteAllButton.setOnClickListener {
            showDeleteAllConfirmation()
        }
        
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun observeViewModel() {
        viewModel.allCalculations.observe(viewLifecycleOwner) { calculations ->
            if (calculations.isEmpty()) {
                binding.emptyStateView.visibility = View.VISIBLE
                binding.historyRecyclerView.visibility = View.GONE
                binding.deleteAllButton.isEnabled = false
            } else {
                binding.emptyStateView.visibility = View.GONE
                binding.historyRecyclerView.visibility = View.VISIBLE
                binding.deleteAllButton.isEnabled = true
                historyAdapter.submitList(calculations)
            }
        }
    }
    
    private fun navigateToResult(calculation: CalculationResult) {
        val action = HistoryFragmentDirections.actionHistoryToResult(calculation.id)
        findNavController().navigate(action)
    }
    
    private fun showDeleteConfirmation(calculation: CalculationResult) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("删除记录")
            .setMessage("确定要删除这条测算记录吗？")
            .setPositiveButton("删除") { _, _ ->
                viewModel.deleteCalculation(calculation)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showDeleteAllConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("删除全部")
            .setMessage("确定要删除所有测算记录吗？此操作不可恢复。")
            .setPositiveButton("删除全部") { _, _ ->
                viewModel.deleteAllCalculations()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
