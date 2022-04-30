package com.example.healtho.userdetails

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.healtho.R
import com.example.healtho.databinding.FragmentUsernameBinding
import com.example.healtho.dialogs.noInternetDialog.NoInternetDialogFragment
import com.example.healtho.util.BaseFragment
import com.example.healtho.util.Objects.OPEN_NO_INTERNET_DIALOG
import com.example.healtho.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class UsernameFragment : BaseFragment<FragmentUsernameBinding>(FragmentUsernameBinding::inflate) {

    private val viewModel: UserDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.usernameInput.doOnTextChanged { text, _, _, _ ->
            viewModel.onUserNameChange(text.toString())
        }
        binding.nextButton.setOnClickListener {
            viewModel.onUsernameNextButtonClicked()
        }

        collectUiState()
        collectUiEvents()
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.usernameUiState.collect {
                binding.nextButton.isEnabled = it.isNextButtonEnabled
                binding.loadingLayout.loadingLayout.isVisible = it.isLoading
            }
        }
    }

    private fun collectUiEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.usernameEvents.collect {
                when (it) {
                    GetUserNameScreenEvents.NavigateToNextScreen -> {
                        findNavController().navigate(R.id.action_usernameFragment_to_userWeightFragment)
                    }
                    is GetUserNameScreenEvents.ShowToast -> requireContext().showToast(it.message)
                    GetUserNameScreenEvents.ShowNoInternetDialog -> openNoInternetDialog()
                }
            }
        }
    }

    private fun openNoInternetDialog() {
        NoInternetDialogFragment().show(parentFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }

}