package com.example.healtho.homescreens.dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healtho.R
import com.example.healtho.databinding.FragmentDashboardBinding

import com.example.healtho.util.BaseFragment
import com.example.healtho.util.setMarginTopForFullScreen
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import timber.log.Timber

@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(FragmentDashboardBinding::inflate) {

    private lateinit var dashboardPagerAdapter: DashboardPagerAdapter
    private val viewModel by viewModels<DashboardViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.headerText.setMarginTopForFullScreen()
        dashboardPagerAdapter = DashboardPagerAdapter(requireActivity())
        initViewPager()
        collectUserInfo()

//        binding.statsButton.setOnClickListener {
//            findNavController().navigate(R.id.action_dashboardFragment_to_statsActivity)
//        }
//        binding.logoutButton.setOnClickListener {
//            viewModel.logoutComplete()
//            findNavController().navigate(R.id.action_dashboardFragment_to_authActivity)
//        }
    }

    private fun collectUserInfo() = lifecycleScope.launchWhenStarted {
        viewModel.user.collect { user ->
            binding.headerText.text = "Hey ${user?.username} !"
            }
    }


    private fun initViewPager() {
        binding.viewpager.adapter = dashboardPagerAdapter
        TabLayoutMediator(binding.tableLayout, binding.viewpager) { tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Water"
                }
                1 -> {
                    tab.text = "Sleep"
                }
            }
        }.attach()
    }
}