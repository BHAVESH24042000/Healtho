package com.example.healtho.userdetails

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healtho.R
import com.example.healtho.databinding.FragmentUserWeightBinding
import com.example.healtho.dialogs.noInternetDialog.NoInternetDialogFragment
import com.example.healtho.util.BaseFragment
import com.example.healtho.util.Objects.OPEN_NO_INTERNET_DIALOG
import com.example.healtho.util.setMarginTopForFullScreen
import com.example.healtho.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserWeightFragment : BaseFragment<FragmentUserWeightBinding>(FragmentUserWeightBinding::inflate) {

    private val viewModel: UserDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.nextButton.setOnClickListener {
            viewModel.onUserWeightNextButtonPressed( binding.etWeight.text.toString())
        }
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.backButton.setMarginTopForFullScreen()
        collectUiState()
        collectEvents()
    }

    private fun collectEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userWeightEvents.collect {
                when (it) {
                    UserWeightScreenEvents.NavigateToNextScreen -> {
                        findNavController().navigate(R.id.action_userWeightFragment_to_userAgeFragment)
                    }
                    is UserWeightScreenEvents.ShowToast -> requireContext().showToast(it.message)
                    UserWeightScreenEvents.ShowNoInternetDialog -> openNoInternetDialog()
                }
            }
        }
    }

    private fun openNoInternetDialog() {
        NoInternetDialogFragment().show(parentFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userWeightUiState.collect {
                binding.nextButton.isEnabled = it.isButtonEnabled
                binding.loadingLayout.loadingLayout.isVisible = it.isLoading
            }
        }
    }

}