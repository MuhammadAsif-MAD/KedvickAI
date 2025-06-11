package com.kedvik.ai.ui.helpAndSupport.activities

import android.os.Bundle
import android.view.View
import com.kedvik.ai.databinding.ActivityHelpAndSupportBinding
import com.kedvik.ai.ui.auth.activities.BaseActivity
import com.kedvik.ai.ui.dashboard.fragments.DashboardFragment
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.NetworkUtils
import com.kedvik.ai.utils.SessionAndCookies

class HelpAndSupportActivity : BaseActivity() {

    private var token = ""
    private var unreadCounts: Int = 0
    private lateinit var binding: ActivityHelpAndSupportBinding

    override fun onResume() {
        super.onResume()
        callNotificationCountsApi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpAndSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@HelpAndSupportActivity

        token = SessionAndCookies.getUserToken(context) ?: ""

        setData()
        initViews()
        setObserver()
    }

    private fun setData() {
        unreadCounts = DashboardFragment.unreadCounts ?: 0
        if (unreadCounts != 0) {
            binding.rlUnreadCounts.visibility = View.VISIBLE
            binding.tvUnreadCounts.text = unreadCounts.toString()
        } else {
            binding.rlUnreadCounts.visibility = View.GONE
        }
    }

    private fun initViews() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.btnMyReports.setOnClickListener {
            Constant.startMyReportsActivity(context)
        }
        binding.btnCreateReport.setOnClickListener {
            Constant.startCreateReportActivity(context)
        }
        binding.btnFAQs.setOnClickListener {
            Constant.startFaqsActivity(context)
        }
    }

    private fun callNotificationCountsApi() {
        if (NetworkUtils.isInternetAvailable(context)) {
            mainViewModel.callNotificationCountsApi(
                context,
                token,
                binding.layoutLoader.layoutLoader
            )
        }
    }

    private fun setObserver() {
        mainViewModel.notificationCounts.observe(this) { event ->
            event.getContentIfNotHandled()?.let { dataModel ->
                if (dataModel.status == true) {
                    if (dataModel.data != null && dataModel.data.ticketUnreadCounter != 0) {
                        binding.rlUnreadCounts.visibility = View.VISIBLE
                        binding.tvUnreadCounts.text = dataModel.data.ticketUnreadCounter.toString()
                        DashboardFragment.unreadCounts = dataModel.data.ticketUnreadCounter
                    } else {
                        binding.rlUnreadCounts.visibility = View.GONE
                    }
                } else {
                    binding.rlUnreadCounts.visibility = View.GONE
                    CustomCookieToast(context).showFailureToast(
                        dataModel.title ?: "",
                        dataModel.errors?.get(0)?.message ?: ""
                    )
                }
            }
        }
    }
}