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
import com.kedvick.ai.databinding.ActivityDeleteAccountBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.SessionAndCookies
import com.kedvick.ai.utils.Utilities

class DeleteAccountActivity : BaseActivity() {

    lateinit var myId: String
    lateinit var password: String
    private var token = ""

    lateinit var binding: ActivityDeleteAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        setClickListener()
        editTextWatcher()
        setObserver()
    }

    private fun initData() {
        Constant.setWindowToAdjustResize(context)
        myId = SessionAndCookies.getMyId(context)!!
        token = SessionAndCookies.getUserToken(context) ?: ""
    }

    private fun setClickListener() {
        binding.edPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }

        binding.icPasswordEye.setOnClickListener {
            Constant.setPasswordTransformationMethod(binding.edPassword, binding.icPasswordEye)
        }
        binding.btnDeleteAcconut.setOnClickListener {
            checkValidations()
        }
    }

    private fun checkValidations() {
        password = binding.edPassword.text.toString().trim()

        if (TextUtils.isEmpty(password)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterYourPassword))
        } else {
            // Api Call
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            mainViewModel.callDeleteUserApi(context, token, password, binding.layoutLoader.layoutLoader)
        }
    }

    private fun editTextWatcher() {
        binding.edPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text: String = s.toString()
                if (text.contains(" ")) {
                    val trimmedText = text.trim()
                    if (text != trimmedText) {
                        binding.edPassword.setText(trimmedText)
                        binding.edPassword.setSelection(trimmedText.length)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    private fun setObserver() {
        mainViewModel.deleteUser.observe(this) { event ->
            event.getContentIfNotHandled()?.let { it ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE

                if (it.status!!) {
                    CustomCookieToast(context).showSuccessToast(it.action?:"")

                    Handler(Looper.myLooper()!!).postDelayed({
                        SessionAndCookies.clearSessionAndCookies(context)
                        Constant.startSignInFinishScreen(context, "", "")
                    }, 2000)
                } else {
                    CustomCookieToast(context).showFailureToast(it.action?:"")
                }
            }
        }
    }
}