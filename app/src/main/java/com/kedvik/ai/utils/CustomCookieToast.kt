package com.kedvik.ai.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.kedvik.ai.R
import org.aviran.cookiebar2.CookieBar


class CustomCookieToast(var context: Activity) {

    private val DURATION = 3000;

    fun showSuccessToast(msg: String) {
        CookieBar.build(context)
            .setCustomView(R.layout.custom_banner)
            .setCustomViewInitializer { view: View ->
                val cookie = view.findViewById<LinearLayout>(R.id.ll_cookie)
                val ivIcon = view.findViewById<ImageView>(R.id.iv_icon)
                val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                val tvMessage = view.findViewById<TextView>(R.id.tv_message)
                tvTitle.setText(R.string.success)
                tvMessage.text = msg
                ivIcon.setImageResource(R.drawable.ic_baseline_sentiment_satisfied)
                cookie.setBackgroundResource(R.drawable.bg_10sdp_app_color)
            }
            .setCookiePosition(CookieBar.TOP)
            .setDuration(DURATION.toLong())
            .setEnableAutoDismiss(true)
            .setSwipeToDismiss(true)
            .show()
    }

    fun showSuccessToast(context: Activity, title: String, msg: String) {
        CookieBar.build(context).setCustomView(R.layout.custom_banner)
            .setCustomViewInitializer { view: View ->
                val cookie = view.findViewById<LinearLayout>(R.id.ll_cookie)
                val ivIcon =
                    view.findViewById<ImageView>(R.id.iv_icon)
                val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                val tvMessage = view.findViewById<TextView>(R.id.tv_message)
                tvTitle.text = title
                tvMessage.text = msg
                ivIcon.setImageResource(R.drawable.ic_baseline_sentiment_satisfied)
                cookie.setBackgroundResource(R.drawable.bg_10sdp_app_color)
            }
            .setCookiePosition(CookieBar.TOP)
            .setDuration(DURATION.toLong())
            .setEnableAutoDismiss(true) // Cookie will stay on display until manually dismissed
            .setSwipeToDismiss(true) // Deny dismiss by swiping off the view
            .show()
    }

    fun showFailureToast(msg: String?) {
        CookieBar.build(context).setCustomView(R.layout.custom_banner)
            .setCustomViewInitializer { view: View ->
                val cookie = view.findViewById<LinearLayout>(R.id.ll_cookie)
                val ivIcon =
                    view.findViewById<ImageView>(R.id.iv_icon)
                val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                val tvMessage = view.findViewById<TextView>(R.id.tv_message)
                tvTitle.setText(R.string.error)
                tvMessage.text = msg
                ivIcon.setImageResource(R.drawable.ic_warning_sign)
                cookie.setBackgroundResource(R.drawable.bg_10sdp_red_color)
            }
            .setCookiePosition(CookieBar.TOP)
            .setDuration(DURATION.toLong())
            .setEnableAutoDismiss(true) // Cookie will stay on display until manually dismissed
            .setSwipeToDismiss(true) // Deny dismiss by swiping off the view
            .show()
    }

    fun showFailureToast(title: String, msg: String) {
        CookieBar.build(context).setCustomView(R.layout.custom_banner)
            .setCustomViewInitializer { view: View ->
                val cookie = view.findViewById<LinearLayout>(R.id.ll_cookie)
                val ivIcon =
                    view.findViewById<ImageView>(R.id.iv_icon)
                val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                val tvMessage = view.findViewById<TextView>(R.id.tv_message)
                tvTitle.text = title
                tvMessage.text = msg
                ivIcon.setImageResource(R.drawable.ic_baseline_warning_24)
                cookie.setBackgroundResource(R.drawable.bg_10sdp_red_color)
            }
            .setCookiePosition(CookieBar.TOP)
            .setDuration(DURATION.toLong())
            .setEnableAutoDismiss(true) // Cookie will stay on display until manually dismissed
            .setSwipeToDismiss(true) // Deny dismiss by swiping off the view
            .show()
    }

    @SuppressLint("SetTextI18n")
    fun showRequiredToast(pleaseEnter: String) {
        CookieBar.build(context).setCustomView(R.layout.custom_banner)
            .setCustomViewInitializer { view: View ->
                val cookie = view.findViewById<LinearLayout>(R.id.ll_cookie)
                val ivIcon =
                    view.findViewById<ImageView>(R.id.iv_icon)
                val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                val tvMessage = view.findViewById<TextView>(R.id.tv_message)
                tvTitle.setText(R.string.required)
                tvMessage.text = pleaseEnter
                ivIcon.setImageResource(R.drawable.ic_warning_sign)
                cookie.setBackgroundResource(R.drawable.bg_10sdp_app_color)
            }
            .setCookiePosition(CookieBar.TOP)
            .setDuration(DURATION.toLong())
            .setEnableAutoDismiss(true) // Cookie will stay on display until manually dismissed
            .setSwipeToDismiss(true) // Deny dismiss by swiping off the view
            .show()
    }

    @SuppressLint("SetTextI18n")
    fun showNoInternetToast() {
        CookieBar.build(context).setCustomView(R.layout.custom_banner)
            .setCustomViewInitializer { view: View ->
                val cookie = view.findViewById<LinearLayout>(R.id.ll_cookie)
                val ivIcon =
                    view.findViewById<ImageView>(R.id.iv_icon)
                val tvTitle = view.findViewById<TextView>(R.id.tv_title)
                val tvMessage = view.findViewById<TextView>(R.id.tv_message)
                tvTitle.text = "Cannot connect to Internet!"
                tvMessage.text = "Make sure you have internet connection and Try again!"
                //ivIcon.setImageResource(R.drawable.ic_baseline_warning_24);
                ivIcon.setImageResource(R.drawable.ic_baseline_wifi_off_24)
                cookie.setBackgroundResource(R.drawable.bg_10sdp_red_color)
            }
            .setCookiePosition(CookieBar.TOP)
            .setDuration(DURATION.toLong())
            .setEnableAutoDismiss(true) // Cookie will stay on display until manually dismissed
            .setSwipeToDismiss(true) // Deny dismiss by swiping off the view
            .show()
    }

}