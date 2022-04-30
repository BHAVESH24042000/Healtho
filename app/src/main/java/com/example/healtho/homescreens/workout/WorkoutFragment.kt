package com.example.healtho.homescreens.workout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healtho.R
import com.example.healtho.databinding.FragmentSleepDashboardBinding
import com.example.healtho.databinding.FragmentWorkoutBinding
import com.example.healtho.dialogs.noInternetDialog.NoInternetDialogFragment
import com.example.healtho.util.BaseFragment
import com.example.healtho.util.InternetChecker
import com.example.healtho.util.Objects
import com.example.healtho.util.Objects.workoutString
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WorkoutFragment : BaseFragment<FragmentWorkoutBinding>(FragmentWorkoutBinding::inflate) {
    private val viewModel by viewModels<WorkoutViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fotdText.text = workoutString
        collectUserInfo()
        binding.flStart.setOnClickListener {
            if(!InternetChecker(requireContext()).hasInternetConnection()){
                NoInternetDialogFragment().show(parentFragmentManager,
                    Objects.OPEN_NO_INTERNET_DIALOG
                )
            }else {
                findNavController().navigate(R.id.action_workoutFragment_to_exerciseFragment)
            }
        }
    }

    private fun collectUserInfo() = lifecycleScope.launchWhenStarted {
        viewModel.user.collect { user ->
            binding.headerText.text = "Hey ${user?.username} !"
        }
    }
}