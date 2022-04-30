package com.example.healtho.homescreens.dashboard

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.healtho.homescreens.dashboard.sleep.SleepDashboardFragment
import com.example.healtho.homescreens.dashboard.water.WaterDashboardFragment

class DashboardPagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WaterDashboardFragment()
            else -> SleepDashboardFragment()
        }
    }
}
