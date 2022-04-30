package com.example.healtho.homescreens.stats.workoutstats

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healtho.R
import com.example.healtho.databinding.FragmentWorkoutStatsBinding
import com.example.healtho.homescreens.stats.waterstats.WaterStatsViewModel
import com.example.healtho.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class WorkoutStatsFragment :  BaseFragment<FragmentWorkoutStatsBinding>(FragmentWorkoutStatsBinding::inflate)  {

    private val viewModel by viewModels<WorkoutStatsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureBarChart()
        configureLineChart()
        collectUiState()
    }

    private fun collectUiState() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.uiState.collect {
            Timber.d(it.toString())
            binding.percentage.text = "${it.weeklyPercentage}%"
            //binding.exp.text = it.expGained.toString()
            binding.barChart.animate(it.barChartData)
            binding.lineChart.animate(it.lineChartData)
            binding.weekDate.text = it.weekDate
        }
    }

    private fun configureLineChart() {
        binding.lineChart.apply {
            animation.duration = 1000L
            labelsFormatter = {
                "${it.toInt()}%"
            }
            gradientFillColors =
                intArrayOf(
                    resources.getColor(R.color.colorPrimaryDark),
                    Color.TRANSPARENT
                )
            labelsFont = ResourcesCompat.getFont(requireContext(), R.font.raleway_medium)
            labelsColor = resources.getColor(R.color.colorPrimaryDark)
            labelsSize =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12F, resources.displayMetrics)
        }
    }

    private fun configureBarChart() {
        binding.barChart.apply {
            animation.duration = 1000L
            labelsFormatter = {
                val value = it.toInt()
                "$value"
            }
            labelsFont = ResourcesCompat.getFont(requireContext(), R.font.raleway_medium)
            labelsColor = resources.getColor(R.color.colorPrimary)
            labelsSize =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12F, resources.displayMetrics)
//            onDataPointClickListener = { i: Int, _: Float, _: Float ->
//                val message = "${data[i].count} tasks completed on last ${data[i].dayFull}"
//                requireContext().showToast(message)
//            }
        }
    }
}