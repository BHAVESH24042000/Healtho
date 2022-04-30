package com.example.healtho.homescreens.dashboard.water

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healtho.adapters.WaterLogAdapter
import com.example.healtho.databinding.FragmentWaterDashboardBinding
import com.example.healtho.dialogs.AddWaterDialogFragment
import com.example.healtho.dialogs.noInternetDialog.NoInternetDialogFragment
import com.example.healtho.util.BaseFragment
import com.example.healtho.util.Objects.OPEN_ADD_WATER_DIALOG
import com.example.healtho.util.Objects.OPEN_NO_INTERNET_DIALOG
import com.example.healtho.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaterDashboardFragment : BaseFragment<FragmentWaterDashboardBinding>(FragmentWaterDashboardBinding::inflate) {
    private val viewModel: WaterDashboardViewModel by viewModels()
    private val waterAdapter = WaterLogAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addWater.setOnClickListener {
            viewModel.onAddWaterPressed()
        }
        binding.drinkLogRv.apply {
            setHasFixedSize(false)
            adapter = waterAdapter
        }

        collectUiState()
        collectUiEvents()
    }

    private fun collectUiEvents() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.events.collect {
            when (it) {
                WaterDashboardScreenEvents.OpenAddWaterDialog -> openAddWaterDialog()
                is WaterDashboardScreenEvents.ShowToast -> requireContext().showToast(it.message)
                WaterDashboardScreenEvents.CreateAlarm -> createAlarm()
                WaterDashboardScreenEvents.ShowNoInternetDialog -> showNoInternetDialog()
            }
        }
    }
    val ALARM_CODE = 15
    private fun createAlarm() {
//        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(requireContext(), WaterBroadcastReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(
//            requireContext(),
//            ALARM_CODE,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        alarmManager.set(
//            AlarmManager.RTC_WAKEUP,
//            System.currentTimeMillis() + 60000, //1miniute in millisecs
//            pendingIntent
//        )
    }

    private fun collectUiState() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.uiState.collect {
            binding.apply {
                greetingText.text = it.greeting
                fotdText.text = it.factOfTheDay
                completedText.text = it.completedAmount.toString()
                totalText.text = "/ ${it.totalAmount} mL"
                addWater.isEnabled = it.isAddWaterButtonEnabled
                loadingLayout.loadingLayout.isVisible = it.isLoading
                waterAdapter.submitList(it.waterLog)
                yayText.text = it.mainGreeting

            }
        }
    }

    private fun openAddWaterDialog() {
        AddWaterDialogFragment(onAmountSelected = {
            viewModel.onWaterSelected(it)
        }, onDismiss = {
            viewModel.onDialogClosed()
        }).show(parentFragmentManager, OPEN_ADD_WATER_DIALOG)
    }



    private fun showNoInternetDialog() {
        NoInternetDialogFragment().show(parentFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }
}