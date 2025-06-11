package com.kedvik.ai.ui.settings.inviteFriends.activities

import android.os.Bundle
import com.kedvik.ai.databinding.ActivityInviteFriendsBinding
import com.kedvik.ai.ui.auth.activities.BaseActivity
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.utils.Utilities

class InviteFriendsActivity : BaseActivity() {

    private lateinit var referralCode: String
    private lateinit var binding: ActivityInviteFriendsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInviteFriendsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@InviteFriendsActivity

        initData()
        setClickListener()
    }

    private fun initData() {
        referralCode = SessionAndCookies.getReferralCode(context)?:""
        binding.tvReferralCode.text = referralCode
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.btnCopyReferralCode.setOnClickListener {
            Utilities.textCopyToClipboard(context, referralCode)
        }
        binding.btnInviteFromContacts.setOnClickListener {
            Constant.startContactsListActivity(context)
        }
        binding.btnInviteBySms.setOnClickListener {
            Utilities.sendSmsIntent(context, referralCode, packageName)
        }
        binding.btnInviteByEmail.setOnClickListener {
            Utilities.sendEmailIntent(context, referralCode, packageName)
        }
        binding.btnInviteByShare.setOnClickListener {
            Utilities.shareAppIntent(context, referralCode, packageName)
        }
    }
}