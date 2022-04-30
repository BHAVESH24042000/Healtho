package com.example.healtho.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healtho.homescreens.MainActivity
import com.example.healtho.userdetails.UserDetailActivity
import com.example.healtho.databinding.FragmentGettingStartedBinding
import com.example.healtho.util.BaseFragment
import com.example.healtho.util.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GettingStartedFragment : BaseFragment<FragmentGettingStartedBinding>(FragmentGettingStartedBinding::inflate) {

    private val viewModel by viewModels<GettingStartedViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnLogin.setOnClickListener {
            loginUser()
        }
        binding.btnRegister.setOnClickListener {
            registerUser()
        }
        collectUiState()
        collectUiEvents()
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect {
                binding.btnLogin.isEnabled = it.isButtonEnabled
                binding.loadingLayout.loadingLayout.isVisible = it.isLoading
            }
        }
    }

    private fun collectUiEvents() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect {
                when (it) {
                    is GettingStartedScreenEvents.ShowToast -> requireContext().showToast(it.message)
                    GettingStartedScreenEvents.Logout -> logoutUser()
                    GettingStartedScreenEvents.NavigateToUserDetailsScreen -> navigateToUserDetailsScreen()
                    GettingStartedScreenEvents.NavigateToHomeScreen -> navigateToHomeScreen()
                }
            }
        }
    }

    private fun loginUser() {
        viewModel.startLoading()
        if(validateUser()) {
            val email = binding.etEmail.text.toString().trim { it <= ' ' }
            val password = binding.etPassword.text.toString().trim { it <= ' ' }
            viewModel.tryLogin(email, password)

        } else{
            viewModel.stopLoading()
            //requireContext().showToast("Email or Password Format is not Correct")
        }
    }

    private fun registerUser() {
        viewModel.startLoading()
        if(validateUser()) {
            val email = binding.etEmail.text.toString().trim { it <= ' ' }
            val password = binding.etPassword.text.toString().trim { it <= ' ' }
            viewModel.tryRegister(email, password)

        } else{
            viewModel.stopLoading()
            //requireContext().showToast("Email or Password Format is not Correct")
        }
    }



    private fun logoutUser() {
     viewModel.logoutComplete()
    }

    private fun navigateToHomeScreen() {
        Intent(requireContext(), MainActivity::class.java).also { intent ->
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun navigateToUserDetailsScreen() {
        Intent(requireContext(), UserDetailActivity::class.java).also { intent ->
            startActivity(intent)
            requireActivity().finish()
        }
    }

    private fun validateUser(): Boolean {
        return when{
            TextUtils.isEmpty(binding.etEmail.text.toString().trim {it<=' ' }) ->{
                requireContext().showToast("Please Enter Your Email ID")
                false
            }
            TextUtils.isEmpty(binding.etPassword.text.toString().trim {it<=' ' }) ->{
                requireContext().showToast("Please Enter Your Password")
                false
            }
            else ->{
                true
            }
        }
    }

}