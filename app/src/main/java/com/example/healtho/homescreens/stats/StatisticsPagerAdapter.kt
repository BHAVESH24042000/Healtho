package com.example.healtho.homescreens.stats

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.healtho.homescreens.stats.sleepstats.SleepStatsFragment
import com.example.healtho.homescreens.stats.waterstats.WaterStatsFragment
import com.example.healtho.homescreens.stats.workoutstats.WorkoutStatsFragment

class StatisticsPagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> WaterStatsFragment()
            1 -> SleepStatsFragment()
            else -> WorkoutStatsFragment()

        }
    }
}
