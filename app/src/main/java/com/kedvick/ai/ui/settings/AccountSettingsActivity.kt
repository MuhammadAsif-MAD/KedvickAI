package com.kedvick.ai.ui.settings

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivityAccountSettingsBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.SessionAndCookies
import com.kedvick.ai.utils.Utilities

class AccountSettingsActivity : BaseActivity() {

    lateinit var myId: String
    private var token = ""
    private var isPasswordAdded: Boolean = false
    private lateinit var binding: ActivityAccountSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAccountSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@AccountSettingsActivity

        initData()
        setClickListener()
        setObserver()
    }

    private fun initData() {
        myId = SessionAndCookies.getMyId(context) ?: ""
        token = SessionAndCookies.getUserToken(context) ?: ""
        isPasswordAdded = SessionAndCookies.getBoolean(context, Constant.IS_PASSWORD_ADDED)
        if (isPasswordAdded) {
            binding.rlChangePassword.visibility = View.VISIBLE
            binding.rlSetPassword.visibility = View.GONE
        } else {
            binding.rlChangePassword.visibility = View.GONE
            binding.rlSetPassword.visibility = View.VISIBLE
        }
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.btnEditProfile.setOnClickListener {
            Constant.startEditProfileActivity(context)
        }
        binding.btnChangePassword.setOnClickListener {
            Constant.startChangePasswordActivity(context)
        }
        binding.btnSetPassword.setOnClickListener {
            Constant.startSetPasswordActivity(context)
        }
        binding.btnDeleteAccount.setOnClickListener {
            if (!isPasswordAdded) {
                alertDeleteAccount()
            } else {
                Constant.startDeleteAccountActivity(context)
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun alertDeleteAccount() {
        val alertDialog: AlertDialog
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.item_popup, null)
        builder.setView(customLayout)
        val tvAlert = customLayout.findViewById<TextView>(R.id.tv_alert)
        val tvAlertDescription = customLayout.findViewById<TextView>(R.id.tv_alert_desc)
        val txtYes = customLayout.findViewById<TextView>(R.id.txtYes)
        val txtNo = customLayout.findViewById<TextView>(R.id.txtNo)
        tvAlert.text = "Do you want to delete your account?"
        tvAlertDescription.text =
            "You will lose all of your data by deleting your\naccount. This action cannot be undone."
        txtYes.text = "Yes, Delete"
        txtYes.setTextColor(ContextCompat.getColor(context, R.color.dark_red_color))
        txtNo.text = "Cancel"
        alertDialog = builder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.show()
        alertDialog.setCancelable(false)
        txtYes.setOnClickListener {
            alertDialog.dismiss()
            callDeleteSocialAccount()
        }
        txtNo.setOnClickListener { alertDialog.dismiss() }
    }

    private fun callDeleteSocialAccount() {
        // Api Call
        Utilities.hideKeyboard(context)
        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
        mainViewModel.callDeleteSocialAccountApi(context, token, binding.layoutLoader.layoutLoader)
    }

    private fun setObserver() {
        mainViewModel.deleteSocialAccount.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
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