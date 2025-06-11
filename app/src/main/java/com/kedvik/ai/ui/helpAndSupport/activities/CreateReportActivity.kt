package com.kedvik.ai.ui.helpAndSupport.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import com.jaredrummler.materialspinner.MaterialSpinner
import com.kedvik.ai.R
import com.kedvik.ai.databinding.ActivityCreateReportBinding
import com.kedvik.ai.ui.auth.activities.BaseActivity
import com.kedvik.ai.ui.helpAndSupport.models.TicketCategoriesDataItemModel
import com.kedvik.ai.ui.helpAndSupport.models.TicketCreateResponseModel
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.utils.Utilities

class CreateReportActivity : BaseActivity() {

    lateinit var myId: String
    private var type: String = ""
    private var typeId: String = ""
    lateinit var description: String
    var token: String = ""
    private var categoriesList: MutableList<String> = arrayListOf()
    private var categoriesModelList: MutableList<TicketCategoriesDataItemModel> = arrayListOf()
    private lateinit var binding: ActivityCreateReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        callTicketCategoriesApi()
        setObserver()
    }
    private fun initData() {
        context = this@CreateReportActivity
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        myId = SessionAndCookies.getMyId(context)!!
        token = SessionAndCookies.getUserToken(context)?:""

        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.mSpinnerComplaint.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener { _: MaterialSpinner?, _: Int, _: Long, item: String ->
            type = item

            for (element in categoriesModelList) {
                if (element.name == type){
                    typeId = element.id
                }
            }
        } as MaterialSpinner.OnItemSelectedListener<String>)

        binding.btnSendReport.setOnClickListener {
            description = binding.edComplainDescription.text.toString().trim()
            if (TextUtils.isEmpty(type)){
                CustomCookieToast(context).showRequiredToast(Constant.PLEASE_SELECT_COMPLAIN_CATEGORY)
            }else if (!TextUtils.isEmpty(description)) {
                // Api Call
                Utilities.hideKeyboard(context)
                binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
                mainViewModel.callTicketCreateApi(context, token, typeId, description, binding.layoutLoader.layoutLoader)
            } else {
                CustomCookieToast(context).showRequiredToast(Constant.ENTER_SOME_COMPLAINT_DESCRIPTION_TO_PROCEED)
            }
        }

    }

    private fun callTicketCategoriesApi() {
        // Api Call
        Utilities.hideKeyboard(context)
        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
        mainViewModel.callTicketCategoriesApi(context, token, binding.layoutLoader.layoutLoader)
    }

    private fun setObserver() {
        mainViewModel.ticketCategories.observe(this)  { event ->
            event.getContentIfNotHandled()?.let { dataModel ->
                if (dataModel.status == true) {
                    binding.layoutLoader.layoutLoader.visibility = View.GONE

                    categoriesModelList = dataModel.data
                    for (element in dataModel.data) {
                        categoriesList.add(element.name)
                    }
                    binding.mSpinnerComplaint.setItems(categoriesList)
                } else {
                    CustomCookieToast(context).showFailureToast(dataModel.action?:"")
                }
            }
        }
        mainViewModel.ticketCreate.observe(this)  { event ->
            event.getContentIfNotHandled()?.let { dataModel ->
                if (dataModel.status == true) {
                    binding.layoutLoader.layoutLoader.visibility = View.GONE
                    CustomCookieToast(context).showSuccessToast(dataModel.action!!)

                    val model: TicketCreateResponseModel = dataModel
                    Handler(Looper.myLooper()!!).postDelayed({
                        val intent = Intent(context, SupportChatActivity::class.java)
                        intent.putExtra(Constant.TICKET_ID, model.data!!.uuid + "")
                        intent.putExtra(Constant.TICKET_STATUS, model.data.status)
                        intent.putExtra(Constant.CALL_FROM, Constant.NEW_COMPLAINT)
                        startActivity(intent)
                        overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
                        finish()
                    }, 700)
                } else {
                    CustomCookieToast(context).showFailureToast(dataModel.action?:"")
                }
            }
        }
    }
}