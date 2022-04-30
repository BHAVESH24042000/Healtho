package com.example.healtho

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.healtho.auth.AuthActivity
import com.example.healtho.auth.GettingStartedViewModel
import com.example.healtho.homescreens.MainActivity
import com.vaibhav.chatofy.util.makeStatusBarTransparent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val viewModel: GettingStartedViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        makeStatusBarTransparent()
        viewModel.getUser()
        animateLogo()
        Handler(Looper.getMainLooper()).postDelayed({
            navigateUser()
        }, 5000)
    }

    private fun animateLogo() {
        val animation = AnimationUtils.loadAnimation(this, R.anim.scale_logo)
        findViewById<TextView>(R.id.logo).apply {
            startAnimation(animation)
        }
    }

    private fun navigateUser() = lifecycleScope.launchWhenStarted {

               viewModel.userLoggedIn.collect { user ->
                if (user != null) {

                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    this@SplashActivity.finish()
                } else {

                    val intent = Intent(this@SplashActivity, AuthActivity::class.java)
                    startActivity(intent)
                    this@SplashActivity.finish()
                }
            }

    }
}

/*

androidx.lifecycle RepeatOnLifecycleKt.class public suspend fun Lifecycle.repeatOnLifecycle(
    state: Lifecycle.State,
    block: suspend CoroutineScope.() → Unit
): Unit
Runs the given block in a new coroutine when this Lifecycle is at least at state and suspends the execution until this Lifecycle is Lifecycle.State.DESTROYED.
The block will cancel and re-launch as the lifecycle moves in and out of the target state.
class MyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        /* ... */
            // Runs the block of code in a coroutine when the lifecycle is at least STARTED.
            // The coroutine will be cancelled when the ON_STOP event happens and will
            // restart executing if the lifecycle receives the ON_START event again.
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    uiStateFlow.collect { uiState ->
                        updateUi(uiState)
                    }
                }
            }
        }
    }
The best practice is to call this function when the lifecycle is initialized. For example, onCreate in an Activity, or onViewCreated in a Fragment. Otherwise, multiple repeating coroutines doing the same could be created and be executed at the same time.
Repeated invocations of block will run serially, that is they will always wait for the previous invocation to fully finish before re-starting execution as the state moves in and out of the required state.
Warning: Lifecycle.State.INITIALIZED is not allowed in this API. Passing it as a parameter will throw an IllegalArgumentException.
Params:
state - Lifecycle.State in which block runs in a new coroutine. That coroutine will cancel if the lifecycle falls below that state, and will restart if it's in that state again.
block - The block to run when the lifecycle is at least in state state.
  Gradle: androidx.lifecycle:lifecycle-runtime-ktx:2.5.0-beta01@aar
 */