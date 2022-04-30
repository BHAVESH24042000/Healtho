package com.example.healtho.homescreens.stats.sleepstats

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healtho.R
import com.example.healtho.databinding.FragmentSleepStatsBinding
import com.example.healtho.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SleepStatsFragment:  BaseFragment<FragmentSleepStatsBinding>(FragmentSleepStatsBinding::inflate) {

    private val viewModel by viewModels<SleepStatsViewModel>()

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
        }
    }
}