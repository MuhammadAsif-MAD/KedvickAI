package com.kedvick.ai.ui.settings

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivitySetPasswordBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.SessionAndCookies
import com.kedvick.ai.utils.Utilities

class SetPasswordActivity : BaseActivity() {
    private lateinit var myEmail: String
    private lateinit var newPassword: String
    private lateinit var binding: ActivitySetPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@SetPasswordActivity

        editTextWatcher()
        initData()
        setClickListener()
        setObserver()
    }

    private fun editTextWatcher() {
        binding.edNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text: String = binding.edNewPassword.text.toString()
                if (text.contains(" ")) {
                    binding.edNewPassword.setText(text.trim())
                    if (!TextUtils.isEmpty(text.trim())){
                        binding.edNewPassword.setSelection(text.trim().length)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    private fun initData() {
        Constant.setWindowToAdjustResize(context)

        myEmail = SessionAndCookies.getUserEmail(context)?:""
    }

    private fun setClickListener() {
        binding.edNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.icNewPasswordEye.setOnClickListener {
            Constant.setPasswordTransformationMethod(binding.edNewPassword, binding.icNewPasswordEye)
        }
        binding.btnSetPassword.setOnClickListener {
            checkValidations()
        }
    }

    private fun checkValidations() {
        newPassword = binding.edNewPassword.text.toString().trim()

        if (TextUtils.isEmpty(newPassword)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.please_enter_new_password))
        } else if (newPassword.length < 6 || newPassword.length > 30) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PasswordMin6Max30Characters))
        } else {
            // Api Call
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            mainViewModel.callResetPasswordApi(context, myEmail, newPassword, binding.layoutLoader.layoutLoader)
        }
    }

    private fun setObserver() {
        mainViewModel.authResetPassword.observe(this)  { event ->
            event.getContentIfNotHandled()?.let { it ->
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