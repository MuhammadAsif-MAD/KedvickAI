package com.kedvick.ai.ui.dashboard.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.kedvick.ai.ui.dashboard.adapters.ParentFragmentAdapter
import com.kedvick.ai.R
import com.kedvick.ai.api.ApplicationClass
import com.kedvick.ai.databinding.ActivityDashboardBinding
import com.kedvick.ai.ui.dashboard.fragments.DashboardFragment
import com.kedvick.ai.ui.dashboard.fragments.ImageFragment
import com.kedvick.ai.ui.dashboard.fragments.ResearchFragment
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.NetworkUtils
import com.kedvick.ai.utils.SessionAndCookies
import com.kedvick.ai.utils.Utilities
import com.kedvick.ai.viewmodel.MainViewModel
import com.kedvick.ai.viewmodel.MainViewModelFactory

class DashboardActivity : AppCompatActivity(), DashboardFragment.OnFragmentItemClickedInterface {


    private lateinit var fragmentList: ArrayList<Fragment>
    private lateinit var fragmentAdapter: ParentFragmentAdapter

    private lateinit var mainViewModel: MainViewModel

    var context = this@DashboardActivity

    private var isPopupShowing: Boolean = false
    var pressedOnce: Boolean = false
    private lateinit var myId: String
    private lateinit var referralCode: String
    private var isServiceUsed: Boolean = false
    private var isReviewAdded: Boolean = false
    private lateinit var binding: ActivityDashboardBinding

    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            initPlayStoreReviewData()
        }, 1500)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@DashboardActivity

        val repository = (application as ApplicationClass).apiRepository
        mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]

        initData()
        initViewPager()
        onBackPressCallback()
        setObserver()
    }

    private fun initData() {
        myId = SessionAndCookies.getMyId(context) ?: ""
        referralCode = SessionAndCookies.getReferralCode(context) ?: ""

        if (NetworkUtils.isInternetConnectionAvailable(context)) {
            mainViewModel.callHomeSplashApi(context, myId)
        }
    }

    private fun initPlayStoreReviewData() {
        isServiceUsed = SessionAndCookies.getBoolean(context, Constant.IS_SERVICE_USED)
        isReviewAdded = SessionAndCookies.getPopupBoolean(context, Constant.IS_REVIEW_ADDED)

        if (isServiceUsed && !isReviewAdded && !isPopupShowing) {
            isPopupShowing = true
            popupPlayStoreFeedback()
        }
    }

    private fun setObserver() {
        mainViewModel.homeSplashApi.observe(this) { event ->
            event.getContentIfNotHandled()?.let { dataModel ->
                SessionAndCookies.saveString(
                    context,
                    Constant.PAYSTACK_SECRET,
                    dataModel.paystackKeySecret
                )
                SessionAndCookies.saveString(
                    context,
                    Constant.CURRENCY_EXCHANGE_VALUE,
                    dataModel.currencyExchange
                )
            }
        }
    }

    private fun initViewPager() {
        fragmentList = ArrayList()

        fragmentList.add(DashboardFragment())
        fragmentList.add(ResearchFragment())
        fragmentList.add(ImageFragment())

        fragmentAdapter = ParentFragmentAdapter(this@DashboardActivity, fragmentList)
        binding.viewPager2.adapter = fragmentAdapter
        binding.viewPager2.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.viewPager2.isUserInputEnabled = false

        binding.llDashboard.setOnClickListener {
            setFragmentAndTabColors(binding.imgDashboard, 0)
        }
        binding.llResearch.setOnClickListener {
            Constant.startChatActivity(context, Constant.NEW_CHAT)
        }
        binding.llImage.setOnClickListener {
            setFragmentAndTabColors(binding.imgImage, 2)
        }
    }

    private fun setFragmentAndTabColors(tabImage: ImageView, tabValue: Int) {
        binding.imgDashboard.setImageResource(R.drawable.dashboard_unselected_ic)
        binding.imgResearch.setImageResource(R.drawable.research_tab_ic)
        binding.imgImage.setImageResource(R.drawable.image_unselected_ic)

        binding.txtDashboard.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.txtResearch.setTextColor(ContextCompat.getColor(this, R.color.white))
        binding.txtImage.setTextColor(ContextCompat.getColor(this, R.color.white))

        when (tabValue) {
            0 -> {
                tabImage.setImageResource(R.drawable.dashboard_selected_ic)
                binding.txtDashboard.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            1 -> {
                tabImage.setImageResource(R.drawable.research_tab_ic)
                binding.txtResearch.setTextColor(ContextCompat.getColor(context, R.color.white))
            }

            else -> {
                tabImage.setImageResource(R.drawable.image_selected_ic)
                binding.txtImage.setTextColor(ContextCompat.getColor(context, R.color.white))
            }
        }

        binding.viewPager2.setCurrentItem(tabValue, false)

    }

    private fun onBackPressCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (binding.viewPager2.currentItem != 0) {
                    setFragmentAndTabColors(binding.imgDashboard, 0)
                } else {
                    if (pressedOnce) {
                        finishAffinity()
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
                    } else {
                        Toast.makeText(context, "Press back again to exit", Toast.LENGTH_SHORT)
                            .show()
                        pressedOnce = true
                    }
                    Handler(Looper.getMainLooper()).postDelayed({ pressedOnce = false }, 2000)
                }
            }
        })
    }

    override fun onItemClicked(position: Int) {
        when (position) {
            0 -> {
                Constant.startChatActivity(context, Constant.NEW_CHAT)
            }

            1 -> {
                setFragmentAndTabColors(binding.imgImage, 3)
            }

            2 -> {
                Constant.startListOfAvailableSpeechActivity(context)
            }

            else -> {
                Utilities.shareAppIntent(context, referralCode, packageName)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun popupPlayStoreFeedback() {
        val alertDialog: AlertDialog
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.item_feedback_popup, null)
        builder.setView(customLayout)

        val tvILoveIt = customLayout.findViewById<TextView>(R.id.tv_i_love_it)
        val tvNoThisSucks = customLayout.findViewById<TextView>(R.id.tv_no_this_sucks)
        val tvDismiss = customLayout.findViewById<TextView>(R.id.tvDismiss)

        alertDialog = builder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.show()
        alertDialog.setCancelable(false)
        tvILoveIt.setOnClickListener {
            alertDialog.dismiss()
            SessionAndCookies.savePopupBoolean(context, Constant.IS_REVIEW_ADDED, true)
            // show review sheet
            Utilities.launchInAppReview(context)
        }
        tvNoThisSucks.setOnClickListener { alertDialog.dismiss() }
        tvDismiss.setOnClickListener { alertDialog.dismiss() }
    }

}