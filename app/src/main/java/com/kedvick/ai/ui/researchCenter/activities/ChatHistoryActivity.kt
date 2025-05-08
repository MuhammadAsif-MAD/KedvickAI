package com.kedvick.ai.ui.researchCenter.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.ui.researchCenter.adapter.ChatHistoryAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivityChatHistoryBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.ui.researchCenter.model.ChatHistoryRecordsItemModel
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.SessionAndCookies

class ChatHistoryActivity : BaseActivity(), ChatHistoryAdapter.ItemClickedInterface {

    private var modelList: MutableList<ChatHistoryRecordsItemModel> = arrayListOf()
    private var token: String = ""
    private var myId: String = ""
    private var selectedPosition: Int = 0
    private lateinit var rvAdapter: ChatHistoryAdapter
    private lateinit var binding: ActivityChatHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@ChatHistoryActivity

        initData()
        setClickListener()
        callChatHistoryApi()
        setObserver()
    }

    private fun initData() {
        myId = SessionAndCookies.getMyId(context) ?: ""
        token = SessionAndCookies.getUserToken(context)?:""
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    private fun callChatHistoryApi() {
        binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        mainViewModel.callChatHistoryApi(context, token, myId, binding.layoutLoader.layoutLoader)
    }

    private fun setObserver() {
        mainViewModel.chatHistory.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                modelList = data.records

                setAdapter()
            }
        }

        mainViewModel.deleteChatHistory.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if(data.status != false && data.message == "Chat history deleted"){
                    rvAdapter.itemRemoved(selectedPosition)
                }else{
                    CustomCookieToast(context).showFailureToast(data.title?:"", data.errorsList?.get(0)?.message ?:"")
                }
            }
        }
    }

    private fun setAdapter() {
        if (modelList.size == 0) {
            binding.llEmptyListAvatar.visibility = View.VISIBLE
        } else {
            binding.llEmptyListAvatar.visibility = View.GONE
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvAdapter = ChatHistoryAdapter(context, modelList, this)
        binding.recyclerView.adapter = rvAdapter
    }

    override fun onItemClicked(position: Int) {
        val dataList = modelList[position].data
        val chatId = modelList[position].chatId?:""
        Constant.startChatActivity(context, chatId, Constant.CHAT_HISTORY, dataList)
    }

    override fun onOptionButtonClick(view: View, position: Int) {
        selectedPosition = position
        val chatId = modelList[position].chatId?:""
        showPopupMenu(view, chatId)
    }

    private fun showPopupMenu(view: View, chatId: String) {
        val popupMenu = PopupMenu(this, view, Gravity.END, 0, R.style.CustomPopupMenu)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.delete_chat_menu, popupMenu.menu)

        // Optionally force icons to show
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenu.setForceShowIcon(true)
        }

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_delete -> {
                    displayDeleteChatPopup(context, chatId)
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }
    @SuppressLint("SetTextI18n")
    private fun showBottomSheet(chatId: String) {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val sheetView: View = LayoutInflater.from(context)
            .inflate(R.layout.bottom_sheet_2_options, context.findViewById(R.id.bottom_sheet))
        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()
        bottomSheetDialog.dismissWithAnimation = true

        sheetView.findViewById<View>(R.id.rlOne).setOnClickListener {
            bottomSheetDialog.dismiss()
            displayDeleteChatPopup(context, chatId)
        }
        sheetView.findViewById<View>(R.id.rlCancel).setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayDeleteChatPopup(context: Activity, chatId: String) {
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.item_delete_pdf_popup, null)
        builder.setView(customLayout)

        val txtAlert = customLayout.findViewById<TextView>(R.id.tv_alert)
        val txtAlertDescription = customLayout.findViewById<TextView>(R.id.tv_alert_desc)
        val txtYes = customLayout.findViewById<TextView>(R.id.txtYes)
        val txtNo = customLayout.findViewById<TextView>(R.id.txtNo)

        txtAlert.text = "Do you want to delete this chat?"
        txtAlertDescription.text = "Are you sure you want to delete this chat? This action cannot be undone."

        val permissionDialog: AlertDialog = builder.create()
        permissionDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        permissionDialog.setCancelable(false)
        permissionDialog.show()
        txtYes.setOnClickListener {
            permissionDialog.dismiss()

            callDeleteDocumentApi(chatId)
        }
        txtNo.setOnClickListener { permissionDialog.dismiss() }
    }

    private fun callDeleteDocumentApi(chatId: String) {
        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
        mainViewModel.callDeleteChatHistoryApi(context, token, chatId, binding.layoutLoader.layoutLoader)
    }
}