package com.kedvik.ai.ui.helpAndSupport.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvik.ai.ui.helpAndSupport.adapters.AdapterSupport
import com.kedvik.ai.databinding.ActivitySupportRequestsBinding
import com.kedvik.ai.ui.auth.activities.BaseActivity
import com.kedvik.ai.ui.helpAndSupport.models.ItemSupportReq
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.utils.Utilities

@Suppress("DEPRECATION")
class SupportRequestsActivity : BaseActivity(), AdapterSupport.ItemClickedInterface {

    lateinit var myId: String
    var modelList: List<ItemSupportReq> = arrayListOf()
    lateinit var binding: ActivitySupportRequestsBinding

    override fun onResume() {
        super.onResume()
        binding.animationView.visibility = View.GONE
        callTicketCategoriesApi(false)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportRequestsBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        initData()
        setUpUi()
        callTicketCategoriesApi(true)
        setObserver()
    }

    private fun initData() {
        context = this@SupportRequestsActivity
    }

    private fun setUpUi() {
        binding.btnCreateRequest.setOnClickListener {
            Constant.startCreateSupportActivity(context)
        }
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemClicked(position: Int, dataModel: ItemSupportReq) {
        val intent = Intent(this, SupportDetailActivity::class.java)
        intent.putExtra("SUPPORT", dataModel)
        startActivity(intent)
    }

    private fun callTicketCategoriesApi(isLoaderShow: Boolean) {
        myId = SessionAndCookies.getMyId(context)?:""
        // Api Call
        Utilities.hideKeyboard(context)
        if (isLoaderShow){
            binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
        }else{
            if (modelList.isEmpty()){
                binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
            }
        }
        mainViewModel.callSupportRequestListApi(context, myId, binding.layoutLoader.layoutLoader)
    }

    private fun setObserver() {
        mainViewModel.supportRequestList.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                modelList = data
                if (modelList.isEmpty()){
                    binding.animationView.visibility = View.VISIBLE
                }else{
                    binding.animationView.visibility = View.GONE
                }

                setAdapter()
            }
        }
    }

    private fun setAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = AdapterSupport(context, modelList, this)

        var isOpen = false
        for (req in modelList) {
            if (req.status.equals("Open") || req.status.equals("Replied") || req.status.equals("Pending")) {
                isOpen = true
                break
            }
        }
        if (isOpen) {
            binding.rlCreateRequest.visibility = View.GONE
        } else {
            binding.rlCreateRequest.visibility = View.VISIBLE
        }
    }
}