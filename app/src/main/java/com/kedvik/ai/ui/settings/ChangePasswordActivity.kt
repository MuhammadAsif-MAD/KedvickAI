package com.kedvik.ai.ui.settings

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import com.kedvik.ai.R
import com.kedvik.ai.databinding.ActivityChangePasswordBinding
import com.kedvik.ai.ui.auth.activities.BaseActivity
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.utils.Utilities

class ChangePasswordActivity : BaseActivity() {
    private lateinit var myId: String
    private lateinit var oldPassword: String
    private lateinit var newPassword: String
    private lateinit var binding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@ChangePasswordActivity

        editTextWatcher()
        initData()
        setClickListener()
        setObserver()
    }

    private fun editTextWatcher() {
        binding.edOldPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text: String = binding.edOldPassword.text.toString()
                if (text.contains(" ")) {
                    binding.edOldPassword.setText(text.trim())
                    if (!TextUtils.isEmpty(text.trim())){
                        binding.edOldPassword.setSelection(text.trim().length)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

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

        myId = SessionAndCookies.getMyId(context)!!
        // deviceId = SessionAndCookies.getDeviceId(context)!!
        /*userPlatform = SessionAndCookies.getUserPlatform(context)!!

        if (userPlatform == "Google" || userPlatform == "Apple") {
            binding.rlSave.visibility = View.GONE
            binding.bottomView.visibility = View.GONE
            binding.edOldPassword.isEnabled = false
            binding.edNewPassword.isEnabled = false
            binding.icOldPasswordEye.isEnabled = false
            binding.icNewPasswordEye.isEnabled = false

            binding.rlSocialError.visibility = View.VISIBLE
        }*/
    }

    private fun setClickListener() {
        binding.edOldPassword.transformationMethod = PasswordTransformationMethod.getInstance()
        binding.edNewPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.icOldPasswordEye.setOnClickListener {
            Constant.setPasswordTransformationMethod(binding.edOldPassword, binding.icOldPasswordEye)
        }
        binding.icNewPasswordEye.setOnClickListener {
            Constant.setPasswordTransformationMethod(binding.edNewPassword, binding.icNewPasswordEye)
        }
        binding.btnUpdatePassword.setOnClickListener {
            checkValidations()
        }
    }

    private fun checkValidations() {
        oldPassword = binding.edOldPassword.text.toString().trim()
        newPassword = binding.edNewPassword.text.toString().trim()

        if (TextUtils.isEmpty(oldPassword)) {
            CustomCookieToast(context).showRequiredToast("Please enter your old password")
        } else if (TextUtils.isEmpty(newPassword)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.please_enter_new_password))
        } else if (newPassword.length < 6 || newPassword.length > 30) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PasswordMin6Max30Characters))
        } else {
            // Api Call
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            mainViewModel.callChangePasswordApi(context, myId, oldPassword, newPassword, binding.layoutLoader.layoutLoader)
        }
    }

    private fun setObserver() {
        mainViewModel.changePassword.observe(this)  { event ->
            event.getContentIfNotHandled()?.let { it ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if (it.status == true) {
                    CustomCookieToast(context).showSuccessToast(it.action?:"")

                    /*Handler(Looper.myLooper()!!).postDelayed({
                        SessionAndCookies.clearSessionAndCookies(context)
                        Constant.startSignInFinishScreen(context, "", "")
                    }, 2000)*/
                } else {
                    CustomCookieToast(context).showFailureToast(it.action?:"")
                }
            }
        }
    }

}