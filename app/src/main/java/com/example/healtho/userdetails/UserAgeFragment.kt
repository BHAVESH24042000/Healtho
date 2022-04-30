package com.example.healtho.userdetails

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healtho.homescreens.MainActivity
import com.example.healtho.databinding.FragmentUserAgeBinding
import com.example.healtho.dialogs.noInternetDialog.NoInternetDialogFragment
import com.example.healtho.util.BaseFragment
import com.example.healtho.util.Objects.OPEN_NO_INTERNET_DIALOG
import com.example.healtho.util.setMarginTopForFullScreen
import com.example.healtho.util.showToast
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserAgeFragment : BaseFragment<FragmentUserAgeBinding>(FragmentUserAgeBinding::inflate) {
    private val viewModel by viewModels<UserDetailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.userAgeProgressBar.setProgress(viewModel.userAgeUiState.value.age.toFloat())
        binding.continueBtn.setOnClickListener {
            viewModel.onContinueButtonPressed()
        }
        binding.backButton.setMarginTopForFullScreen()
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.userAgeProgressBar.onSeekChangeListener = object : OnSeekChangeListener {
            override fun onSeeking(seekParams: SeekParams?) {
                seekParams?.let { viewModel.onAgeChange(it.progress) }
            }

            override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) = Unit
        }

        collectUiState()
        collectUIEvents()
    }

    private fun collectUIEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userAgeUiState.collect {
                binding.userAge.text = "${it.age} yrs"
                binding.continueBtn.isEnabled = it.isButtonEnabled
                binding.loadingLayout.loadingLayout.isVisible = it.isLoading
            }
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userAgeEvents.collect {
                when (it) {
                    UserAgeScreenEvents.NavigateToHomeScreen -> navigateToHomeScreen()
                    is UserAgeScreenEvents.ShowToast -> requireContext().showToast(it.message)
                    UserAgeScreenEvents.ShowNoInternetDialog -> openNoInternetDialog()
                }
            }
        }
    }

    private fun navigateToHomeScreen() {
        Intent(requireContext(), MainActivity::class.java).also {
            startActivity(it)
            requireActivity().finish()
        }
    }

    private fun openNoInternetDialog() {
        NoInternetDialogFragment().show(parentFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }
}