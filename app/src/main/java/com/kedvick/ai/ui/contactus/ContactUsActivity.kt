package com.kedvick.ai.ui.contactus

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivityContactUsBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.ui.contactus.adapter.ContactUsCategoriesAdapter
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.SessionAndCookies
import com.kedvick.ai.utils.Utilities

class ContactUsActivity : BaseActivity(), ContactUsCategoriesAdapter.ItemClickedInterface {

    var myId: String = ""
    var name: String = ""
    var email: String = ""
    var message: String = ""
    var category: String = "Purchase Error"

    private lateinit var adapterCategory: ContactUsCategoriesAdapter

    private lateinit var binding: ActivityContactUsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactUsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@ContactUsActivity

        initData()
        Constant.setWindowToAdjustResize(context)
        editTextWatcher()
        setClickListener()
        setAdapter()
        setObserver()
    }

    private fun initData() {
        myId = SessionAndCookies.getMyId(context)?:""
        name = SessionAndCookies.getUserName(context)?:""
        email = SessionAndCookies.getUserEmail(context)?:""

        binding.edName.setText(name)
        binding.edEmail.setText(email)
    }

    private fun editTextWatcher() {
        binding.edEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text: String = binding.edEmail.text.toString()
                if (text.startsWith(" ")) {
                    binding.edEmail.setText(text.trim())
                    if (!TextUtils.isEmpty(text.trim())) {
                        binding.edEmail.setSelection(text.trim().length)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.btnSubmit.setOnClickListener {
            checkValidations()
        }
    }

    private fun checkValidations() {
        name = binding.edName.text.toString().trim()
        email = binding.edEmail.text.toString().trim()
        message = binding.edMessage.text.toString().trim()
        binding.edEmail.requestFocus()

        if (TextUtils.isEmpty(name)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterYourName))
        }else if (TextUtils.isEmpty(email)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterYourEmail))
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterValidEmail))
        } else if (TextUtils.isEmpty(message)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterMessage))
        }  else {
            // Api Call
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            mainViewModel.callContactUsApi(context, name, email, message,
                category, binding.layoutLoader.layoutLoader)
        }
    }

    private fun setAdapter() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        adapterCategory = ContactUsCategoriesAdapter(context, this)
        binding.recyclerView.setAdapter(adapterCategory)

        val categories: MutableList<String> = ArrayList()
        categories.add("Purchase Error")
        categories.add("Technical Error")
        categories.add("App issue")
        categories.add("Feedback")

        adapterCategory.setCategories(categories)
        category = categories[0]
    }

    private fun setObserver() {
        mainViewModel.contactUs.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                CustomCookieToast(context).showSuccessToast(data.message?:"Message Send Successfully!")

                Handler(Looper.myLooper()!!).postDelayed({
                    onBackPressedCallback.handleOnBackPressed()
                }, 2000)
            }
        }
    }

    override fun onItemClicked(category: String) {
        this.category = category
    }

}