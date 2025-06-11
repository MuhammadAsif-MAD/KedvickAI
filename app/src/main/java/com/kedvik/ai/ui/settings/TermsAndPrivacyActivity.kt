package com.kedvik.ai.ui.settings

import android.os.Bundle
import com.kedvik.ai.databinding.ActivityTermsAndPrivacyBinding
import com.kedvik.ai.ui.auth.activities.BaseActivity
import com.kedvik.ai.utils.Constant

class TermsAndPrivacyActivity : BaseActivity() {

    private lateinit var binding: ActivityTermsAndPrivacyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsAndPrivacyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@TermsAndPrivacyActivity

        setClickListener()

    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.btnPrivacyPolicy.setOnClickListener {
            Constant.startWebViewActivity(context, Constant.PRIVACY_POLICY, Constant.PRIVACY_POLICY_URL)
        }
        binding.btnTermsAndConditions.setOnClickListener {
            Constant.startWebViewActivity(context, Constant.TERMS_AND_CONDITIONS, Constant.TERMS_AND_CONDITIONS_URL)
        }

    }
}