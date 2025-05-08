package com.kedvick.ai.ui.researchCenter.activities

import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import com.kedvick.ai.api.RetrofitBuilder
import com.kedvick.ai.databinding.ActivityPdfViewerBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.CustomCookieToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class PdfViewerActivity : BaseActivity() {
    private lateinit var pdfText: String
    private lateinit var fileUrl: String
    private lateinit var binding: ActivityPdfViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        setClickListener()
    }

    private fun initData() {
        binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
        fileUrl = intent.getStringExtra(Constant.PDF_URL) ?: ""
        pdfText = intent.getStringExtra("PDF_TEXT") ?: ""

        if (fileUrl.contains(".pdf", ignoreCase = true)) {
            binding.pdfView.visibility = View.VISIBLE
            binding.webView.visibility = View.GONE
            binding.topLayoutLoader.layoutLoader.visibility = View.GONE

            loadPdfFromFile(fileUrl)
        } else if (fileUrl.contains(".doc", ignoreCase = true)) {
            fileUrl = RetrofitBuilder.MEDIA_URL + fileUrl

            loadWebView()
        } else if (fileUrl.contains(".docx", ignoreCase = true)) {
            fileUrl = RetrofitBuilder.MEDIA_URL + fileUrl

            loadWebView()
        } else {
            // Handle other file types or errors
            binding.topLayoutLoader.layoutLoader.visibility = View.GONE
            CustomCookieToast(context).showFailureToast("Unsupported file format")
        }
    }

    private fun loadWebView() {
        binding.pdfView.visibility = View.GONE
        binding.webView.visibility = View.VISIBLE
        binding.topLayoutLoader.layoutLoader.visibility = View.GONE
        setUrlLoadProgressListener()
        binding.webView.loadData(pdfText, "text/html", "UTF-8")

    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    private fun loadPdfFromFile(filePath: String) {
        lifecycleScope.launch {
            val file = File(filePath)
            if (file.exists()) {
                binding.pdfView.fromFile(file)
                    .onLoad {
                        binding.pdfView.visibility = View.VISIBLE
                        binding.webView.visibility = View.GONE
                        binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                        binding.rlLoader.visibility = View.GONE
                    }
                    .load()
            } else {
                // Handle error, maybe show a message to the user
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                binding.rlLoader.visibility = View.GONE
            }
        }
    }

    private fun loadPdfFromUrl(url: String) {
        lifecycleScope.launch {
            val inputStream = fetchPdfStream(url)
            if (inputStream != null) {
                binding.pdfView.fromStream(inputStream).onLoad {
                    binding.pdfView.visibility = View.VISIBLE
                    binding.webView.visibility = View.GONE
                    binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                }.load()
            } else {
                // Handle error, maybe show a message to the user
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
            }
        }
    }

    private suspend fun fetchPdfStream(urlString: String): InputStream? {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
                if (urlConnection.responseCode == 200) {
                    BufferedInputStream(urlConnection.inputStream)
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
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
    private fun setProgressAnimate(pb: ProgressBar, progressTo: Int) {
        val animation = ObjectAnimator.ofInt(pb, "progress", pb.progress, progressTo)
        animation.duration = 500
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }

}
