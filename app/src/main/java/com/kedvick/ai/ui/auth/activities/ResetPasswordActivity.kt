package com.kedvick.ai.ui.auth.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivityResetPasswordBinding
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.SessionAndCookies
import com.kedvick.ai.utils.Utilities

class ResetPasswordActivity : BaseActivity() {

    lateinit var email: String
    lateinit var password: String

    private lateinit var binding: ActivityResetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@ResetPasswordActivity

        Constant.setWindowToAdjustResize(context)
        // Utilities.setActivityFullScreen(context)

        email = intent.getStringExtra(Constant.EMAIL)?:""

        editTextWatcher()
        setClickListener()
        setObserver()
    }

    private fun editTextWatcher() {
        binding.edPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text: String = binding.edPassword.text.toString()
                if (text.contains(" ")) {
                    binding.edPassword.setText(text.trim())
                    if (!TextUtils.isEmpty(text.trim())){
                        binding.edPassword.setSelection(text.trim().length)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    private fun setClickListener() {
        binding.edPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.icPasswordEye.setOnClickListener {
            Constant.setPasswordTransformationMethod(
                binding.edPassword,
                binding.icPasswordEye
            )
        }
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.btnResetPassword.setOnClickListener {
            checkValidations()
        }
        binding.tvSignIn.setOnClickListener {
            Constant.startSignInFinishScreen(context, "", "")
        }
    }

    private fun checkValidations() {
        password = binding.edPassword.text.toString().trim()
        if (TextUtils.isEmpty(password)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterYourPassword))
        } else if (password.length < 6 || password.length > 30) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PasswordMin6Max30Characters))
        } else {
            Utilities.hideKeyboard(context)

            // Api Call
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            mainViewModel.callResetPasswordApi(context, email, password, binding.layoutLoader.layoutLoader)
        }
    }

    private fun setObserver() {
        mainViewModel.authResetPassword.observe(this)  { event ->
            event.getContentIfNotHandled()?.let {
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if (it.status == true) {
                    CustomCookieToast(context).showSuccessToast(it.action?:"")

                    Handler(Looper.myLooper()!!).postDelayed({
                        SessionAndCookies.saveLoginSession(context, false)

                        Constant.startSignInFinishScreen(context, "", "")
                    }, 1500)
                } else {
                    CustomCookieToast(context).showFailureToast(it.title?:"", it.errorsList?.get(0)?.message ?:"")
                }
            }
        }
    }
}