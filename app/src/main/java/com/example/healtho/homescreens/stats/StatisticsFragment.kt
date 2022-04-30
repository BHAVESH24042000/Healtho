package com.example.healtho.homescreens.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healtho.R
import com.example.healtho.databinding.FragmentDashboardBinding
import com.example.healtho.databinding.FragmentStatisticsBinding
import com.example.healtho.homescreens.dashboard.DashboardPagerAdapter
import com.example.healtho.homescreens.dashboard.DashboardViewModel
import com.example.healtho.util.BaseFragment
import com.example.healtho.util.setMarginTopForFullScreen
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>(FragmentStatisticsBinding::inflate) {
    private lateinit var pagerAdapter: StatisticsPagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.headerText.setMarginTopForFullScreen()
        pagerAdapter = StatisticsPagerAdapter(requireActivity())
        initViewPager()

    }

    private fun initViewPager() {
        binding.viewpager.adapter = pagerAdapter
        TabLayoutMediator(binding.tableLayout, binding.viewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Water"
                }
                1 -> {
                    tab.text = "Sleep"
                }
                else ->{
                    tab.text = "Workout"
                }
            }
        }.attach()
    }

}