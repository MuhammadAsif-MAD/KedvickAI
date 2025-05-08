package com.kedvick.ai.ui.auth.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivityForgotPasswordBinding
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.Utilities

class ForgotPasswordActivity : BaseActivity() {

    lateinit var email: String
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@ForgotPasswordActivity

        initData()
        editTextWatcher()
        setClickListener()
        setObserver()
    }

    private fun initData() {
        email = intent.getStringExtra(Constant.EMAIL)?:""
        binding.edEmail.setText(email)
    }

    private fun editTextWatcher() {
        binding.edEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text: String = binding.edEmail.text.toString()
                if (text.startsWith(" ")) {
                    binding.edEmail.setText(text.trim())
                    if (!TextUtils.isEmpty(text.trim())) {
                        binding.edEmail.setSelection(text.trim().length)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.btnSendOtp.setOnClickListener {
            checkValidations()
        }
    }

    private fun checkValidations() {
        email = binding.edEmail.text.toString().trim()

        if (TextUtils.isEmpty(email)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterYourEmail))
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterValidEmail))
        } else {
            // API Call
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            mainViewModel.callForgotPasswordApi(context, email, binding.layoutLoader.layoutLoader)
        }
    }

    private fun setObserver() {
        mainViewModel.authForgotPassword.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if (it.status == true) {
                    CustomCookieToast(context).showSuccessToast(it.action?:"")

                    Handler(Looper.getMainLooper()!!).postDelayed({
                        Constant.startAccountVerificationScreen(context, email, Constant.FORGOT_PASSWORD_SCREEN)
                    }, 1000)
                } else {
                    CustomCookieToast(context).showFailureToast(it.title?:"", it.errorsList?.get(0)?.message ?:"")
                }
            }
        }
    }
}