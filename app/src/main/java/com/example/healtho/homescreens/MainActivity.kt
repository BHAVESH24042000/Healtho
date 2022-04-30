package com.example.healtho.homescreens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.work.*
import com.example.healtho.R
import com.example.healtho.databinding.ActivityMainBinding
import com.example.healtho.dialogs.noInternetDialog.NoInternetDialogFragment
import com.example.healtho.homescreens.dashboard.DashboardFragment
import com.example.healtho.homescreens.profile.ProfileFragment
import com.example.healtho.homescreens.stats.StatsActivity
import com.example.healtho.homescreens.workout.ExerciseFragment
import com.example.healtho.homescreens.workout.WorkoutFragment
import com.example.healtho.util.Objects.OPEN_NO_INTERNET_DIALOG
import com.example.healtho.util.showToast
import com.example.healtho.util.viewBinding
import com.vaibhav.chatofy.util.makeStatusBarTransparent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController
    private lateinit var vibrator: Vibrator
    var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navController = findNavController(R.id.fragmentContainerView3)
        binding.bottomNav.setItemSelected(R.id.dashboardFragment)
        currentFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView3)
        makeStatusBarTransparent()
        binding.bottomNav.setOnItemSelectedListener {

            when(it){
                R.id.dashboardFragment -> openDashboardFragment()
                R.id.workoutFragment-> openWorkoutFragment()
                R.id.profileFragment-> openProfileFragment()
            }
        }

        binding.statsButton.setOnClickListener {
            val intent = Intent(this, StatsActivity::class.java)
            startActivity(intent)
        }
        collectUiEvents()
        collectLoadingState()

    }

    private fun openProfileFragment() {

        val currentFrag = currentFragment?.childFragmentManager?.fragments?.get(0)
        if(currentFrag!=null && currentFrag is WorkoutFragment)
            navController.navigate(R.id.action_workoutFragment_to_profileFragment2)
        else if(currentFrag!=null && currentFrag is DashboardFragment)
            (navController.navigate(R.id.action_dashboardFragment_to_profileFragment2))
    }

    private fun openWorkoutFragment() {
        val currentFrag = currentFragment?.childFragmentManager?.fragments?.get(0)
        if(currentFrag!=null && currentFrag is ProfileFragment)
        navController.navigate(R.id.action_profileFragment2_to_workoutFragment)
        else if(currentFrag!=null && currentFrag is DashboardFragment)
        (navController.navigate(R.id.action_dashboardFragment_to_workoutFragment))
    }

    private fun openDashboardFragment() {
        val currentFrag = currentFragment?.childFragmentManager?.fragments?.get(0)
        if(currentFrag!=null && currentFrag is ProfileFragment)
            navController.navigate(R.id.action_profileFragment2_to_dashboardFragment)
        else if(currentFrag!=null && currentFrag is WorkoutFragment)
            (navController.navigate(R.id.action_workoutFragment_to_dashboardFragment))
        else if(currentFrag!=null && currentFrag is ExerciseFragment)
            (navController.navigate(R.id.action_exerciseFragment_to_dashboardFragment))
    }

    private fun collectLoadingState() {
        lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collect {
                binding.loadingLayout.loadingLayout.isVisible = it
            }
        }
    }

    private fun collectUiEvents() {
        lifecycleScope.launchWhenStarted {
            viewModel.events.collectLatest {
                when (it) {
                    is MainActivityScreenEvents.ShowToast -> showToast(it.message)
                    MainActivityScreenEvents.ShowNoInternetDialog -> showNoInternetDialog()
                }
            }
        }
    }

    private fun showNoInternetDialog() {
        NoInternetDialogFragment().show(supportFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }

}