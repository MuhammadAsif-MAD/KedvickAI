package com.kedvik.ai.ui.auth.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import androidx.annotation.RequiresApi
import com.kedvik.ai.R
import com.kedvik.ai.databinding.ActivitySplashBinding
import com.kedvik.ai.model.SplashResponseModel
import com.kedvik.ai.ui.helpAndSupport.activities.SupportChatActivity
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.NetworkUtils
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.utils.Utilities


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private var currentVersionName: String? = ""
    private var oldVersionName: String? = ""
    private var newVersionName: String? = ""
    private var message: String? = ""

    private var alphaAnimation: AlphaAnimation? = null
    private var alphaAnimation2: AlphaAnimation? = null

    private var tickedId: String? = ""
    private var tickedStatus: String? = ""
    private var chatType: String? = ""
    private var userName: String? = ""
    private lateinit var myId: String

    private var isComingFromNotification = true

    private lateinit var binding: ActivitySplashBinding

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@SplashActivity

        // Get current version name
        val pInfo = context.packageManager.getPackageInfo(packageName, 0)
        currentVersionName = pInfo.versionName

        setLogoAnimation()
        setClickListener()
        moveToNextScreen()
        setObserver()
    }

    private fun setLogoAnimation() {
        alphaAnimation = AlphaAnimation(0.2f, 1.0f)
        alphaAnimation?.setDuration(600)
        alphaAnimation?.setRepeatCount(-1)
        alphaAnimation?.repeatMode = Animation.REVERSE
        binding.ivIcon.startAnimation(alphaAnimation)
        alphaAnimation2 = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation2?.setDuration(500)
    }

    private fun setClickListener() {
        binding.layoutUpdateVersion.btnUpdateApp.setOnClickListener {
            Utilities.updateAppVersion(context)
        }
    }

    private fun moveToNextScreen() {
        myId = SessionAndCookies.getMyId(context)?:""

        if (SessionAndCookies.isUserLoggedIn(context)) {
            if (NetworkUtils.isInternetAvailable(context)) {
                mainViewModel.callSplashApi(context, myId)
            }
        } else {
            Handler(Looper.myLooper()!!).postDelayed({
                startActivity(Intent(context, OnBoardingActivity::class.java))
                overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
                finish()
            }, 1500)
        }
    }

    private fun setObserver() {
        mainViewModel.splashApi.observe(this) { event ->
            event.getContentIfNotHandled()?.let {dataModel->
                checkVersion(dataModel)
            }
        }
    }

    private fun checkVersion(dataModel: SplashResponseModel) {

        val androidModel = dataModel.appVersionsModel?.android

        oldVersionName = androidModel?.old

        newVersionName = androidModel?.new
        message = androidModel?.message
        binding.layoutUpdateVersion.tvDescription.text = androidModel?.message

        Log.d("__old__",oldVersionName!!)
        Log.d("__new__",newVersionName!!)
        Log.d("current",currentVersionName!!)

        SessionAndCookies.saveString(context, Constant.PAYSTACK_SECRET, dataModel.paystackKeySecret)
        SessionAndCookies.saveString(context, Constant.CURRENCY_EXCHANGE_VALUE, dataModel.currencyExchange)

        if (currentVersionName == oldVersionName || currentVersionName == newVersionName) {
            handleLayoutVisibility(SPLASH_LAYOUT_SHOW)
        } else {
            handleLayoutVisibility(VERSION_UPDATE_LAYOUT_SHOW)
        }
    }

    private fun handleLayoutVisibility(layoutValue: Int) {
        when (layoutValue) {
            VERSION_UPDATE_LAYOUT_SHOW -> {
                binding.ivIcon.visibility = View.GONE
                binding.layoutUpdateVersion.rlParent.visibility = View.VISIBLE
            }
            SPLASH_LAYOUT_SHOW -> {
                binding.ivIcon.visibility = View.VISIBLE
                binding.layoutUpdateVersion.rlParent.visibility = View.GONE

                handleIntents()
            }
        }
    }

    private fun handleIntents() {
        if (intent != null && intent.extras != null && isComingFromNotification) {
            // For notification payload handling
            isComingFromNotification = false
            val params = intent.extras
            when (params!!["status"].toString()) {
                "1" -> {
                    // 3 Types of Chats
                    chatType = params["type"].toString()
                    myId = params["user_id"].toString()
                    userName = params["user_name"].toString()
                    tickedId = params["ticket_id"].toString()
                    tickedStatus = params["ticket_status"].toString()


                    val intent = Intent(this, SupportChatActivity::class.java)
                    intent.putExtra(Constant.TICKET_ID, tickedId)
                    intent.putExtra("ticket_position", 0)
                    intent.putExtra(Constant.TICKET_STATUS, tickedStatus)
                    intent.putExtra(Constant.CALL_FROM, Constant.COMPLAINT)
                    startActivity(intent)
                    context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
                }

                else -> {
                    moveToNextScreen()
                }
            }
        } else {
            Constant.startDashboardScreen(context)
        }
    }

    companion object {
        var SPLASH_LAYOUT_SHOW: Int = 4657
        var VERSION_UPDATE_LAYOUT_SHOW: Int = 6767
    }
}