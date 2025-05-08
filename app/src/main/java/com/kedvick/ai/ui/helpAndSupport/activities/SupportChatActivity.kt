package com.kedvick.ai.ui.helpAndSupport.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.ui.helpAndSupport.adapters.TicketChatAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kedvick.ai.R
import com.kedvick.ai.api.ApplicationClass
import com.kedvick.ai.databinding.ActivitySupportChatBinding
import com.kedvick.ai.ui.dashboard.activities.DashboardActivity
import com.kedvick.ai.ui.helpAndSupport.models.TicketDetailDataItemModel
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.SessionAndCookies
import com.kedvick.ai.utils.Utilities
import com.kedvick.ai.viewmodel.MainViewModel
import com.kedvick.ai.viewmodel.MainViewModelFactory

@Suppress("DEPRECATION")
class SupportChatActivity : AppCompatActivity() {

    private lateinit var chatAdapter: TicketChatAdapter
    private lateinit var messagesList: MutableList<TicketDetailDataItemModel>
    var layoutManager: LinearLayoutManager? = null
    lateinit var mainViewModel: MainViewModel
    lateinit var onBackPressedCallback: OnBackPressedCallback


    private var token = ""
    private lateinit var myID: String
    private lateinit var deviceID: String
    lateinit var message: String
    private lateinit var ticketID: String
    private var attachmentType: String = "text"
    var callFrom: String = "service"
    private var ticketStatus = 0

    private var ticketPosition = 0

    companion object {
        var isTicketClosed = false
        var isUnreadCountsUpdated = false
    }

    private lateinit var binding: ActivitySupportChatBinding
    lateinit var context: Activity

    override fun onDestroy() {
        SessionAndCookies.saveBoolean(context, Constant.IS_ON_CHAT_SCREEN, false)
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@SupportChatActivity
        Utilities.setNavigationBarColor(context, R.color.bg_fields)


        initViews()
        setClickListener()
        setObserver()
        callTicketDetailApi()
        onBackPressCallback()
    }

    private fun initViews() {
        myID = SessionAndCookies.getMyId(context)?:""
        token = SessionAndCookies.getUserToken(context)?:""
        deviceID = SessionAndCookies.getDeviceId(context)?:""

        SessionAndCookies.saveBoolean(context, Constant.IS_ON_CHAT_SCREEN, true)

        // viewModel & repository initialize
        val repository = (application as ApplicationClass).apiRepository
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]

        ticketID = intent.getStringExtra(Constant.TICKET_ID)!!
        ticketPosition = intent.getIntExtra("ticket_position", 0)
        ticketStatus = intent.getIntExtra(Constant.TICKET_STATUS, 0)
        callFrom = intent.extras!!.getString(Constant.CALL_FROM)?:""

        messagesList = mutableListOf()

        if (ticketStatus != 0) {
            binding.rlSendLayout.visibility = View.GONE
            binding.rlClose.visibility = View.GONE
        }
    }

    private fun setClickListener() {
        binding.btnClose.setOnClickListener {
            showBottomSheet()
        }
        binding.btnSendMessage.setOnClickListener {
            message = binding.edMessage.text.toString().trim()
            if (!TextUtils.isEmpty(message)) {
                binding.ivSendMsg.visibility = View.GONE
                binding.loadingSendMsg.visibility = View.VISIBLE

                Handler(Looper.myLooper()!!).postDelayed({
                    binding.ivSendMsg.visibility = View.VISIBLE
                    binding.loadingSendMsg.visibility = View.GONE
                }, 900)

                binding.edMessage.setText("")
                val myId = myID
                val currentTimeStamp: String = Utilities.getCurrentTimeStamp()!!
                val dataModel = TicketDetailDataItemModel(myId, "text", currentTimeStamp, message)

                addMessage(dataModel)
                callSendMessagesApi()
            }
        }

        binding.edMessage.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            binding.recyclerView.scrollToPosition(0)
        }
    }

    private fun setObserver() {
        mainViewModel.ticketDetail.observe(this)  { event ->
            event.getContentIfNotHandled()?.let {  dataModel ->
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE

                if (dataModel.status!!) {
                    messagesList = dataModel.data
                    showMessagesToRecyclerView()
                } else {
                    CustomCookieToast(context).showFailureToast(dataModel.action!!)
                }
            }
        }

        mainViewModel.ticketClose.observe(this)  { event ->
            event.getContentIfNotHandled()?.let { dataModel ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if (dataModel.status!!) {
                    if (callFrom == Constant.COMPLAINT || callFrom == Constant.NEW_COMPLAINT){
                        isTicketClosed = true
                    }
                    onBackPressedCallback.handleOnBackPressed()
                } else {
                    CustomCookieToast(context).showFailureToast(dataModel.action!!)
                }
            }}

    }

    private fun callTicketDetailApi() {
        // Api Call
        Utilities.hideKeyboard(context)
        binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
        mainViewModel.callTicketDetailApi(context, token, ticketID, binding.layoutLoader.layoutLoader)
    }

    private fun callSendMessagesApi() {
        // Api Call
        binding.layoutLoader.layoutLoader.visibility = View.GONE
        mainViewModel.callSendTicketMessageApi(context, token, ticketID, attachmentType, message, binding.layoutLoader.layoutLoader)
    }

    private fun showMessagesToRecyclerView() {
        try {
            // Sort in descending order
            messagesList.let { it.sortWith { v1, v2 -> v2.id!!.compareTo(v1.id!!) } }
        } catch (_: Exception) {

        }

        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, true)
        layoutManager!!.stackFromEnd = true
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = layoutManager
        chatAdapter = TicketChatAdapter(context, messagesList, myID)
        binding.recyclerView.adapter = chatAdapter
        binding.recyclerView.scrollToPosition(0)
    }

    private fun addMessage(dataModel: TicketDetailDataItemModel) {
        chatAdapter.add(0, dataModel)
        binding.recyclerView.scrollToPosition(0)
    }

    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val sheetView: View = LayoutInflater.from(context).inflate(R.layout.ticket_bottom_sheet, findViewById(R.id.bottom_sheet_add_post))
        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()
        bottomSheetDialog.dismissWithAnimation = true
        sheetView.findViewById<View>(R.id.rl_back).setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        sheetView.findViewById<View>(R.id.rl_close_ticket).setOnClickListener {
            bottomSheetDialog.dismiss()
            alertCloseTicket()
        }
        sheetView.findViewById<View>(R.id.rl_cancel)
            .setOnClickListener { bottomSheetDialog.dismiss() }
    }

    private fun alertCloseTicket() {
        val builder = AlertDialog.Builder(context)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.single_close_ticket_logout, null)
        builder.setView(customLayout)
        val txtNo = customLayout.findViewById<TextView>(R.id.txtNo)
        val txtYes = customLayout.findViewById<TextView>(R.id.txtYes)
        val alertDialog = builder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.show()
        alertDialog.setCancelable(false)
        txtYes.setOnClickListener {
            alertDialog.dismiss()
            // Api Call
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            mainViewModel.callTicketCloseApi(context, token, ticketID, binding.layoutLoader.layoutLoader)

        }
        txtNo.setOnClickListener { alertDialog.dismiss() }
    }

    private fun onBackPressCallback() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when (callFrom) {
                    "complaints" -> {
                        finish()
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
                    }
                    "via_splash_screen" -> {
                        finish()
                    }
                    "direct_service" -> {
                        val intent = Intent(context, DashboardActivity::class.java)
                        intent.putExtra("fragmentToLoad", 4)
                        startActivity(intent)
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
                        finish()
                    }
                    Constant.NEW_COMPLAINT -> {
                        startActivity(Intent(context, MyReportsActivity::class.java))
                        overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
                        finish()
                    }
                    else -> {
                        finish()
                    }
                }
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

}