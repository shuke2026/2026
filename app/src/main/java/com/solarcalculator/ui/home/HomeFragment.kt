package com.solarcalculator.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.solarcalculator.R
import com.solarcalculator.data.model.CalculationResult
import com.solarcalculator.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var recentCalculationsAdapter: RecentCalculationsAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupButtons()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        recentCalculationsAdapter = RecentCalculationsAdapter(
            onItemClick = { calculation ->
                navigateToResult(calculation)
            },
            onDeleteClick = { calculation ->
                showDeleteConfirmation(calculation)
            }
        )
        
        binding.recentCalculationsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentCalculationsAdapter
        }
    }
    
    private fun setupButtons() {
        binding.newCalculationButton.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_calculator)
        }
        
        binding.viewAllHistoryButton.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_history)
        }
    }
    
    private fun observeViewModel() {
        viewModel.recentCalculations.observe(viewLifecycleOwner) { calculations ->
            if (calculations.isEmpty()) {
                binding.emptyStateView.visibility = View.VISIBLE
                binding.recentCalculationsRecyclerView.visibility = View.GONE
            } else {
                binding.emptyStateView.visibility = View.GONE
                binding.recentCalculationsRecyclerView.visibility = View.VISIBLE
                recentCalculationsAdapter.submitList(calculations.take(5))
            }
        }
        
        viewModel.statistics.observe(viewLifecycleOwner) { stats ->
            stats?.let {
                binding.statisticsCard.visibility = View.VISIBLE
                binding.totalCalculationsText.text = "${it.totalCalculations}次"
                binding.totalCapacityText.text = String.format("%.1fkW", it.totalCapacity)
                binding.totalGenerationText.text = String.format("%.1fkWh", it.totalAnnualGeneration)
            } ?: run {
                binding.statisticsCard.visibility = View.GONE
            }
        }
    }
    
    private fun navigateToResult(calculation: CalculationResult) {
        val action = HomeFragmentDirections.actionHomeToResult(calculation.id)
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
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
