package com.kedvick.ai.ui.helpAndSupport.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.ui.helpAndSupport.adapters.TicketsAdapter
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivityMyReportsBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.ui.helpAndSupport.models.TicketsListDataItemModel
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.SessionAndCookies
import com.kedvick.ai.utils.Utilities

class MyReportsActivity : BaseActivity(), View.OnClickListener {
    private lateinit var activeTicketsList: List<TicketsListDataItemModel>
    private lateinit var closedTicketsList: List<TicketsListDataItemModel>
    lateinit var adapter: TicketsAdapter

    lateinit var myId: String
    private var token = ""
    private var isActiveTabSelected = true

    private lateinit var binding: ActivityMyReportsBinding


    override fun onResume() {
        super.onResume()
        try {
            if (SupportChatActivity.isTicketClosed) {
                SupportChatActivity.isTicketClosed = false
                CustomCookieToast(context).showSuccessToast(
                    context,
                    "Complaint Closed!",
                    "We hope your problem has been resolved."
                )

                callApis()
            }else if (SupportChatActivity.isUnreadCountsUpdated) {
                SupportChatActivity.isUnreadCountsUpdated = false

                callApis()
            }
        } catch (_: Exception) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@MyReportsActivity

        initData()
        setClickListener()
        callApis()
        setObserver()
    }

    private fun initData() {
        myId = SessionAndCookies.getMyId(context)?:""
        token = SessionAndCookies.getUserToken(context)?:""

        binding.txtViewTabActive.setOnClickListener(this)
        binding.txtViewTabClosed.setOnClickListener(this)

        activeTicketsList = ArrayList()
        closedTicketsList = ArrayList()

        binding.recViewTickets.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    private fun callApis() {
        // Api Call
        Utilities.hideKeyboard(context)
        binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
        binding.recViewTickets.visibility = View.GONE
        mainViewModel.callTicketsOpenedListApi(context, token, binding.layoutLoader.layoutLoader)
        mainViewModel.callTicketsClosedListApi(context, token, binding.layoutLoader.layoutLoader)
    }

    private fun setObserver() {
        mainViewModel.ticketsOpenedList.observe(this) { event ->
            event.getContentIfNotHandled()?.let { dataModel ->
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                binding.recViewTickets.visibility = View.VISIBLE
                if (dataModel.status == true) {
                    activeTicketsList = dataModel.data
                    if (isActiveTabSelected) {
                        setAdapter(activeTicketsList)
                    }
                } else {
                    CustomCookieToast(context).showFailureToast(dataModel.action?:"")
                }
            }
        }
        mainViewModel.ticketsClosedList.observe(this) { event ->
            event.getContentIfNotHandled()?.let { dataModel ->
                if (dataModel.status == true) {
                    closedTicketsList = dataModel.data
                    if (!isActiveTabSelected){
                        setAdapter(closedTicketsList)
                    }
                } else {
                    CustomCookieToast(context).showFailureToast(dataModel.action?:"")
                }
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    override fun onClick(v: View) {
        when (v.id) {
            R.id.txtViewTabActive -> {
                activeTabSelected(true)
                isActiveTabSelected = true
                setAdapter(activeTicketsList)
            }
            R.id.txtViewTabClosed -> {
                isActiveTabSelected = false
                activeTabSelected(false)
                setAdapter(closedTicketsList)
            }
        }
    }

    private fun setAdapter(ticketLists: List<TicketsListDataItemModel>?) {
        if (ticketLists != null) {
            adapter = TicketsAdapter(context, ticketLists)
            binding.recViewTickets.adapter = adapter
        }
    }

    private fun activeTabSelected(flag: Boolean) {
        if (flag) {
            binding.txtViewTabActive.setBackgroundResource(R.drawable.bg_main_color_without_stroke_corner_5sdp)
            binding.txtViewTabClosed.setBackgroundResource(R.color.transparent)
            binding.txtViewTabActive.setTextColor(ContextCompat.getColor(context, R.color.white))
            binding.txtViewTabClosed.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            binding.txtViewTabActive.setBackgroundResource(R.color.transparent)
            binding.txtViewTabClosed.setBackgroundResource(R.drawable.bg_main_color_without_stroke_corner_5sdp)
            binding.txtViewTabActive.setTextColor(ContextCompat.getColor(context, R.color.white))
            binding.txtViewTabClosed.setTextColor(ContextCompat.getColor(context, R.color.white))
        }
    }

}