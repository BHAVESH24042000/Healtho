package com.example.healtho.homescreens.stats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.healtho.R
import com.vaibhav.chatofy.util.makeStatusBarTransparent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        navController = findNavController(R.id.fragmentContainerView4)
        makeStatusBarTransparent()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}