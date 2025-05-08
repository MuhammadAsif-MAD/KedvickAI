package com.kedvick.ai.ui.auth.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kedvick.ai.api.ApplicationClass
import com.kedvick.ai.viewmodel.MainViewModel
import com.kedvick.ai.viewmodel.MainViewModelFactory
import com.kedvick.ai.R
import com.kedvick.ai.utils.Utilities

open class BaseActivity : AppCompatActivity() {
    lateinit var mainViewModel: MainViewModel
    lateinit var onBackPressedCallback: OnBackPressedCallback

    lateinit var context: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this@BaseActivity

        // viewModel & repository initialize
        val repository = (application as ApplicationClass).apiRepository
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]
        Utilities.setAppBrightness(context, 0.4f)

        onBackPressCallback()
    }
    
    private fun onBackPressCallback() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}