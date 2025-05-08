package com.kedvick.ai.ui.helpAndSupport.activities

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.ui.helpAndSupport.adapters.FAQsChildAdapter
import com.kedvick.ai.databinding.ActivityFaqsBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.ui.helpAndSupport.models.FAQsDataModel
import com.kedvick.ai.utils.CustomCookieToast

class FaqsActivity : BaseActivity() {
    var adapter: FAQsChildAdapter? = null
    private lateinit var modelList: List<FAQsDataModel>

    private lateinit var binding: ActivityFaqsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFaqsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@FaqsActivity

        setClickListener()
        setObserver()
        callApi()
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    private fun setObserver() {
        modelList = ArrayList()
        binding.recyclerView.visibility = View.GONE

        mainViewModel.listOfFaqs.observe(this) { event ->
            event.getContentIfNotHandled()?.also { dataModel ->
                binding.recyclerView.visibility = View.VISIBLE
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                if (dataModel.status!!) {
                    modelList = dataModel.data
                    binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
                    binding.recyclerView.adapter = FAQsChildAdapter(context, modelList)
                } else {
                    CustomCookieToast(context).showFailureToast(dataModel.action!!)
                }
            }
        }
    }

    private fun callApi() {
        binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
        mainViewModel.callListOfFaqsApi(context, binding.topLayoutLoader.layoutLoader)
    }
}