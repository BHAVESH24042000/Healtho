package com.example.healtho.userdetails

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.healtho.R
import com.example.healtho.databinding.ActivityUserDetailBinding
import com.example.healtho.util.viewBinding
import com.vaibhav.chatofy.util.makeStatusBarTransparent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserDetailActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityUserDetailBinding::inflate)
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        navController = findNavController(R.id.fragmentContainerView2)
        makeStatusBarTransparent()
    }
}