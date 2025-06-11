package com.kedvik.ai.ui.auth.activities

import android.app.Activity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.kedvik.ai.databinding.ActivityAccountCreatedBinding
import com.kedvik.ai.utils.Constant

class AccountCreatedActivity : AppCompatActivity()
{

    lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var context: Activity
    private lateinit var binding: ActivityAccountCreatedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountCreatedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@AccountCreatedActivity

        setClickListener()
        onBackPressCallback()
    }

    private fun setClickListener() {
        binding.btnContinue.setOnClickListener {
            Constant.startDashboardScreen(context)
        }
    }

    private fun onBackPressCallback() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}