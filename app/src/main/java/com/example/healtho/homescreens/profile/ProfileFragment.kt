package com.example.healtho.homescreens.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import coil.size.Scale
import com.example.healtho.R
import com.example.healtho.auth.AuthActivity
import com.example.healtho.databinding.FragmentDashboardBinding
import com.example.healtho.databinding.FragmentProfileBinding
import com.example.healtho.dialogs.AddSleepDialogFragment
import com.example.healtho.dialogs.editWaterLimitDialog.EditWaterLimitFragment
import com.example.healtho.dialogs.noInternetDialog.NoInternetDialogFragment
import com.example.healtho.util.BaseFragment
import com.example.healtho.util.Objects.OPEN_NO_INTERNET_DIALOG
import com.example.healtho.util.Objects.OPEN_SLEEP_LIMIT_DIALOG
import com.example.healtho.util.Objects.OPEN_WATER_LIMIT_DIALOG
import com.example.healtho.util.showDialog
import com.example.healtho.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val viewModel by viewModels<ProfileViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectUiState()
        collectUiEvents()

        binding.logoutButton.setOnClickListener {
            viewModel.onLogoutPressed()
        }

        binding.swipeRefresh.setOnRefreshListener {
            binding.swipeRefresh.isRefreshing = true
            binding.swipeRefresh.isRefreshing = false
        }
        binding.changeSleepLimitBtn.setOnClickListener {
            viewModel.onEditSleepLimitPressed()
        }
        binding.changeWaterLimitBtn.setOnClickListener {
            viewModel.onEditWaterLimitPressed()
        }
    }

    private fun collectUiEvents() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.uiState.collect {
            binding.apply {
                username.text = it.username
                profileImage.loadImageUrl(it.profileImage)
                weightTv.text = "${it.weight} kgs"
                ageTv.text = it.age.toString()
                loadingLayout.loadingLayout.isVisible = it.isLoading
                changeWaterLimitBtn.isEnabled = it.isEditWaterQuantityButtonEnabled
                changeSleepLimitBtn.isEnabled = it.isEditSleepLimitButtonEnabled
            }
        }
    }

    private fun collectUiState() = viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        viewModel.events.collect {
            when (it) {
                ProfileScreenEvents.Logout -> logout()
                ProfileScreenEvents.NavigateToAuthScreen -> navigateToAuthScreen()
                is ProfileScreenEvents.ShowLogoutDialog -> showLogoutDialog(
                    it.title,
                    it.description
                )
                is ProfileScreenEvents.ShowToast -> requireContext().showToast(it.message)
                is ProfileScreenEvents.OpenSleepLimitDialog -> openEditSleepLimitDialog(
                    it.onTimeSelected,
                    it.onDismiss
                )
                is ProfileScreenEvents.OpenWaterLimitDialog -> openEditWaterLimitDialog(
                    it.onQuantitySelected,
                    it.onDismiss
                )
                ProfileScreenEvents.ShowNoInternetDialog -> openNoInternetDialog()
            }
        }
    }

    private fun logout() {
        viewModel.disableLogoutButton()
        viewModel.logoutUser()
        viewModel.onLogoutSuccess()
    }

    private fun showLogoutDialog(title: String, message: String) {
        requireContext().showDialog(title, message, onConfirm = {
            viewModel.onLogoutConfirmed()
        }, onDismiss = {
            viewModel.onDialogClosed()
        })
    }

    private fun openEditSleepLimitDialog(onLimitSelected: (Int) -> Unit, onDismiss: () -> Unit) {
        AddSleepDialogFragment(onLimitSelected, onDismiss)
            .show(parentFragmentManager, OPEN_SLEEP_LIMIT_DIALOG)
    }

    private fun openEditWaterLimitDialog(onLimitSelected: (Int) -> Unit, onDismiss: () -> Unit) {
        EditWaterLimitFragment(onLimitSelected, onDismiss)
            .show(parentFragmentManager, OPEN_WATER_LIMIT_DIALOG)
    }

    private fun openNoInternetDialog() {
        NoInternetDialogFragment().show(parentFragmentManager, OPEN_NO_INTERNET_DIALOG)
    }

    private fun navigateToAuthScreen() {
        Intent(requireContext(), AuthActivity::class.java).also {
            startActivity(it)
            requireActivity().finish()
        }
    }

}

fun ImageView.loadImageUrl(image: String) {
    load(image) {
        scale(Scale.FILL)
        crossfade(true)
        error(R.drawable.avatar)
    }
}