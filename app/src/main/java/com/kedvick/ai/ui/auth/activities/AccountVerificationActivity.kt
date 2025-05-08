package com.kedvick.ai.ui.auth.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivityAccountVerificationBinding
import com.kedvick.ai.model.UserResponseModel
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.Constant.Companion.CALL_FROM
import com.kedvick.ai.utils.Constant.Companion.DEVICE
import com.kedvick.ai.utils.Constant.Companion.DEVICE_ID
import com.kedvick.ai.utils.Constant.Companion.EMAIL
import com.kedvick.ai.utils.Constant.Companion.FCM_TOKEN
import com.kedvick.ai.utils.Constant.Companion.NAME
import com.kedvick.ai.utils.Constant.Companion.PASSWORD
import com.kedvick.ai.utils.Constant.Companion.REFERRAL_CODE
import com.kedvick.ai.utils.Constant.Companion.TIME_ZONE
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.SessionAndCookies
import com.kedvick.ai.utils.Utilities

class AccountVerificationActivity : BaseActivity() {

    lateinit var email: String
    private lateinit var callFrom: String
    private lateinit var userOtp: String
    lateinit var name: String
    lateinit var password: String
    lateinit var device: String
    lateinit var deviceId: String
    private lateinit var timezone: String
    private lateinit var fcmToken: String
    lateinit var referralCode: String

    private lateinit var binding: ActivityAccountVerificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@AccountVerificationActivity

        email = intent.getStringExtra(EMAIL)!!
        callFrom = intent.getStringExtra(CALL_FROM) ?: ""

        if (callFrom == Constant.SIGNUP_SCREEN) {
            name = intent.getStringExtra(NAME) ?: ""
            password = intent.getStringExtra(PASSWORD) ?: ""
            device = intent.getStringExtra(DEVICE) ?: ""
            deviceId = intent.getStringExtra(DEVICE_ID) ?: ""
            timezone = intent.getStringExtra(TIME_ZONE) ?: ""
            fcmToken = intent.getStringExtra(FCM_TOKEN) ?: ""
            referralCode = intent.getStringExtra(REFERRAL_CODE) ?: ""
        }

        binding.tvEmail.text = email

        setClickListener()
        setObserver()
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.btnVerifyAccount.setOnClickListener {
            checkValidation()
        }
        binding.tvSendAgain.setOnClickListener {
            alertResendOtp()
        }
    }

    private fun checkValidation() {
        userOtp = binding.otpView.otp.toString()
        if (userOtp.isEmpty()) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.please_enter_otp))
        } else if (userOtp.length < 6) {
            CustomCookieToast(context).showFailureToast("Incorrect OTP code")
        } else {
            // Api Call
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            mainViewModel.callAuthOtpVerifyApi(
                context,
                email,
                userOtp,
                binding.layoutLoader.layoutLoader
            )
        }
    }

    @SuppressLint("SetTextI18n")
    private fun alertResendOtp() {
        val alertDialog: AlertDialog
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.item_popup, null)
        builder.setView(customLayout)
        customLayout.findViewById<TextView>(R.id.tv_alert)
        customLayout.findViewById<TextView>(R.id.tv_alert_desc)
        val txtYes = customLayout.findViewById<TextView>(R.id.txtYes)
        val txtNo = customLayout.findViewById<TextView>(R.id.txtNo)

        alertDialog = builder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.show()
        alertDialog.setCancelable(false)
        txtYes.setOnClickListener {
            alertDialog.dismiss()

            // API Call
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            if (callFrom == Constant.SIGNUP_SCREEN) {
                mainViewModel.callUserVerifyApi(context, email, binding.layoutLoader.layoutLoader)
            } else {
                mainViewModel.callForgotPasswordApi(
                    context,
                    email,
                    binding.layoutLoader.layoutLoader
                )
            }
        }
        txtNo.setOnClickListener { alertDialog.dismiss() }
    }

    private fun setObserver() {
        mainViewModel.authForgotPassword.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if (it.status == true) {
                    CustomCookieToast(context).showSuccessToast(it.action ?: "")
                } else {
                    CustomCookieToast(context).showFailureToast(
                        it.title ?: "",
                        it.errorsList?.get(0)?.message ?: ""
                    )
                }
            }
        }
        mainViewModel.authRegister.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if (data.status == true) {
                    CustomCookieToast(context).showSuccessToast("User registered successfully")
                    Utilities.setPasswordAddedValue(context, data.data?.passwordAdded ?: 0)
                    Handler(Looper.myLooper()!!).postDelayed({
                        saveLoginData(data)
                    }, 2000)
                } else {
                    CustomCookieToast(context).showFailureToast(
                        data.title ?: "",
                        data.errorsList?.get(0)?.message ?: ""
                    )
                }
            }
        }

        mainViewModel.authUserVerify.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if (it.status == true) {
                    CustomCookieToast(context).showSuccessToast(it.action ?: "")
                } else {
                    CustomCookieToast(context).showFailureToast(
                        it.title ?: "",
                        it.errorsList?.get(0)?.message ?: ""
                    )
                }
            }
        }
        mainViewModel.authOtpVerify.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                if (it.status == true) {
                    if (callFrom == Constant.SIGNUP_SCREEN) {
                        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
                        Utilities.hideKeyboard(context)
                        mainViewModel.callAuthRegisterApi(
                            context,
                            name,
                            email,
                            password,
                            device,
                            deviceId,
                            timezone,
                            fcmToken,
                            referralCode,
                            binding.layoutLoader.layoutLoader
                        )
                    } else {
                        binding.layoutLoader.layoutLoader.visibility = View.GONE
                        CustomCookieToast(context).showSuccessToast(it.action ?: "")
                        Handler(Looper.myLooper()!!).postDelayed({
                            Constant.startResetPasswordScreen(context, email)
                            finish()
                        }, 2000)
                    }
                } else {
                    binding.layoutLoader.layoutLoader.visibility = View.GONE
                    CustomCookieToast(context).showFailureToast(
                        it.title ?: "",
                        it.errorsList?.get(0)?.message ?: ""
                    )
                }
            }
        }
    }

    private fun saveLoginData(dataModel: UserResponseModel) {
        val userData = dataModel.data
        if (userData != null) {
            SessionAndCookies.saveUserProfileData(context, userData)
        }
        SessionAndCookies.saveLoginSession(context, true)

        if (SessionAndCookies.isUserLoggedIn(context)) {
            Constant.startDashboardScreen(context)
        }
    }
}