package com.example.healtho.homescreens.dashboard.sleep

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healtho.R
import com.example.healtho.adapters.SleepLogAdapter
import com.example.healtho.databinding.FragmentSleepDashboardBinding
import com.example.healtho.databinding.FragmentUserAgeBinding
import com.example.healtho.dialogs.AddSleepDialogFragment
import com.example.healtho.dialogs.noInternetDialog.NoInternetDialogFragment
import com.example.healtho.util.BaseFragment
import com.example.healtho.util.Objects.OPEN_ADD_SLEEP_DIALOG
import com.example.healtho.util.Objects.OPEN_NO_INTERNET_DIALOG
import com.example.healtho.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SleepDashboardFragment : BaseFragment<FragmentSleepDashboardBinding>(FragmentSleepDashboardBinding::inflate) {

    private val viewModel: SleepDashboardViewModel by viewModels()
    private val sleepLogAdapter = SleepLogAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addSleep.setOnClickListener {
            viewModel.onAddSleepPressed()
        }
        binding.sleepLogRv.apply {
            setHasFixedSize(false)
            adapter = sleepLogAdapter
        }
        collectUiState()
        collectUiEvents()
    }

    private fun collectUiEvents() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.events.collect {
            when (it) {
                SleepDashboardScreenEvents.OpenAddSleepDialog -> openAddSleepDialog()
                is SleepDashboardScreenEvents.ShowToast -> requireContext().showToast(it.message)
                SleepDashboardScreenEvents.ShowNoInternetDialog -> showNoInternetDialog()
            }
        }
    }

    private fun collectUiState() = viewLifecycleOwner.lifecycleScope.launchWhenCreated {
        viewModel.uiState.collect {
            binding.apply {
                Timber.d(it.toString())
                greetingText.text = it.greeting
                fotdText.text = it.factOfTheDay
                completedText.text = it.completedAmount.toString()
                totalText.text = "/ ${it.totalAmount} hrs"
                addSleep.isEnabled = it.isAddSleepButtonEnabled
                loadingLayout.loadingLayout.isVisible = it.isLoading
                sleepLogAdapter.submitList(it.sleepLog)
                yayText.text = it.mainGreeting
            }
        }
    }

    private fun openAddSleepDialog() {
        AddSleepDialogFragment(
            onTimeSelected = {
                viewModel.onSleepSelected(it)
            },
            onDismiss = {
                viewModel.onDialogClosed()
            }
        ).show(parentFragmentManager, OPEN_ADD_SLEEP_DIALOG)
    }

    private fun showNoInternetDialog() {
          NoInternetDialogFragment().show(parentFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }

}