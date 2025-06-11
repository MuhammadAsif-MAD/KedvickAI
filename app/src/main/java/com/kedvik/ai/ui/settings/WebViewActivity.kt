package com.kedvik.ai.ui.settings

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.kedvik.ai.R
import com.kedvik.ai.databinding.ActivityWebViewBinding
import com.kedvik.ai.utils.Constant


class WebViewActivity : AppCompatActivity() {
    lateinit var callFrom: String
    lateinit var webUrl: String

    lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var binding: ActivityWebViewBinding
    lateinit var context: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@WebViewActivity

        receiveIntent()
        bindViews()
        setClickListener()
        webSettings()
        setUrlLoadProgressListener()
        loadUrl()

        onBackPressCallback()
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    private fun loadUrl() {
        // webUrl = "https://checkout.paystack.com/bv1vatg10svfi78"
        binding.webView.loadUrl(webUrl)
    }

    private fun setUrlLoadProgressListener() {
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                setProgressAnimate(binding.progressBar, progress)
                if (progress == 100) {
                    setProgressAnimate(binding.progressBar, 100)
                    Handler(Looper.myLooper()!!).postDelayed({
                        binding.rlLoader.visibility = View.GONE
                    }, 700)
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun webSettings() {
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        binding.webView.settings.loadWithOverviewMode = true
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.displayZoomControls = false
        binding.webView.settings.useWideViewPort = true
    }

    private fun bindViews() {
        binding.tvHeader.text = callFrom
    }

    private fun receiveIntent() {
        callFrom = intent.getStringExtra(Constant.CALL_FROM) ?: ""
        webUrl = intent.getStringExtra(Constant.WEB_URL) ?: ""
    }

    private fun setProgressAnimate(pb: ProgressBar, progressTo: Int) {
        val animation = ObjectAnimator.ofInt(pb, "progress", pb.progress, progressTo)
        animation.duration = 500
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }

    private fun onBackPressCallback() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }
}