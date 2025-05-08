package com.kedvick.ai.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivitySettingsBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.ui.dashboard.fragments.DashboardFragment
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.DoubleClickListener
import com.kedvick.ai.utils.NetworkUtils
import com.kedvick.ai.utils.SessionAndCookies
import com.kedvick.ai.utils.Utilities

class SettingsActivity : BaseActivity() {

    private var token = ""
    var unreadCounts: Int = 0
    private lateinit var binding: ActivitySettingsBinding

    override fun onResume() {
        super.onResume()
        callNotificationCountsApi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@SettingsActivity

        token = SessionAndCookies.getUserToken(context)?:""

        setData()
       setObserver()
        setClickListener()
    }

    private fun setData() {
        unreadCounts = DashboardFragment.unreadCounts?:0
        if (unreadCounts != 0){
            binding.rlUnreadCounts.visibility = View.VISIBLE
            binding.tvUnreadCounts.text = unreadCounts.toString()
        }else{
            binding.rlUnreadCounts.visibility = View.GONE
        }
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.btnAccountSettings.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                Constant.startAccountSettingActivity(context)
            }
        })
        binding.btnHelpSupport.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                Constant.startHelpAndSupportActivity(context)
            }
        })
        binding.btnContactUs.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                Constant.startContactUsActivity(context)
            }
        })
        binding.btnInviteFriends.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                Constant.startInviteFriendsActivity(context)
            }
        })
        binding.btnTermsPrivacy.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                Constant.startTermsAndPrivacyActivity(context)
            }
        })
        binding.btnLeaveReview.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        })
        binding.btnJoinSocialMedia.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                showBottomSheet()
            }
        })

        binding.btnSignOut.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                alertSignOut()
            }
        })
    }

    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val sheetView: View = LayoutInflater.from(context)
            .inflate(R.layout.join_social_media_bottom_sheet, findViewById(R.id.bottom_sheet))

        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()
        bottomSheetDialog.dismissWithAnimation = true

        sheetView.findViewById<View>(R.id.llFacebook).setOnClickListener {
            bottomSheetDialog.dismiss()
            Utilities.openFacebookGroup(context, Constant.FACEBOOK_LINK)
        }
        sheetView.findViewById<View>(R.id.llWhatsapp).setOnClickListener {
            bottomSheetDialog.dismiss()
            Utilities.openWhatsappGroupJoinLink(context, Constant.WHATSAPP_LINK)
        }
        sheetView.findViewById<View>(R.id.llLinkedin).setOnClickListener {
            bottomSheetDialog.dismiss()
            Utilities.openWebLink(context, Constant.LINKEDIN_LINK)
        }
        sheetView.findViewById<View>(R.id.llThreads).setOnClickListener {
            bottomSheetDialog.dismiss()
            Utilities.openWebLink(context, Constant.THREADS_LINK)
        }
        sheetView.findViewById<View>(R.id.llInstagram).setOnClickListener {
            bottomSheetDialog.dismiss()
            Utilities.openWebLink(context, Constant.INSTAGRAM_LINK)
        }
        sheetView.findViewById<View>(R.id.rlClose).setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun alertSignOut() {
        val alertDialog: AlertDialog
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.item_signout_popup, null)
        builder.setView(customLayout)
        val tvAlert = customLayout.findViewById<TextView>(R.id.tv_alert)
        val tvAlertDescription = customLayout.findViewById<TextView>(R.id.tv_alert_desc)
        val txtYes = customLayout.findViewById<TextView>(R.id.txtYes)
        val txtNo = customLayout.findViewById<TextView>(R.id.txtNo)
        tvAlert.text = "Do you want to sign out \nyour account?"
        tvAlertDescription.text = "You will need to sign in with your password the\nnext time you use the application."
        txtYes.text = "Sign Out"
        txtYes.setTextColor(ContextCompat.getColor(context, R.color.dark_red_color))
        txtNo.text = "Cancel"
        alertDialog = builder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.show()
        alertDialog.setCancelable(false)
        txtYes.setOnClickListener {
            alertDialog.dismiss()
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            CustomCookieToast(context).showSuccessToast("Sign Out successfully")
            Handler(Looper.myLooper()!!).postDelayed({
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                SessionAndCookies.clearSessionAndCookies(context)
                Constant.startSignInFinishScreen(context, "", "")
            }, 1000)
        }
        txtNo.setOnClickListener { alertDialog.dismiss() }
    }

    private fun callNotificationCountsApi() {
        if (NetworkUtils.isInternetAvailable(context)) {
            mainViewModel.callNotificationCountsApi(context, token, binding.layoutLoader.layoutLoader)
        }
    }

    private fun setObserver() {
        mainViewModel.notificationCounts.observe(this) { event ->
            event.getContentIfNotHandled()?.let {dataModel->
                if (dataModel.status == true){
                    if (dataModel.data != null && dataModel.data.ticketUnreadCounter != 0){
                        binding.rlUnreadCounts.visibility = View.VISIBLE
                        binding.tvUnreadCounts.text = dataModel.data.ticketUnreadCounter.toString()
                        DashboardFragment.unreadCounts = dataModel.data.ticketUnreadCounter
                    }else{
                        binding.rlUnreadCounts.visibility = View.GONE
                    }
                }else{
                    binding.rlUnreadCounts.visibility = View.GONE
                    CustomCookieToast(context).showFailureToast(dataModel.title?:"", dataModel.errors?.get(0)?.message ?:"")
                }
            }
        }
    }

}