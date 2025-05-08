package com.kedvick.ai.ui.textToSpeech.activities

import android.Manifest
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.kedvick.ai.R
import com.kedvick.ai.api.RetrofitBuilder
import com.kedvick.ai.databinding.ActivityTextToSpeechDetailsBinding
import com.kedvick.ai.databinding.ItemDeleteTextToSpeechPopupBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.ui.textToSpeech.model.TextToSpeechResponseModel
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.SessionAndCookies
import kotlinx.coroutines.*
import linc.com.amplituda.Amplituda
import java.io.IOException

class TextToSpeechDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityTextToSpeechDetailsBinding
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var handler = android.os.Handler(Looper.getMainLooper())
    private var audioUrl: String? = null
    private var toSpeechResponseModel: TextToSpeechResponseModel? = null

    private var isAudioReady = false
    private var isWaveformReady = false

    private var token: String? = null
    private var textToSpeechId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextToSpeechDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        preloadAudioAndWaveform()
        setOnClickListeners()
        setObserver()
    }

    private fun setObserver() {
        mainViewModel.deleteTextToSpeechList.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                if (data.status != false) {
                    val intent = Intent()
                    intent.putExtra("model", toSpeechResponseModel)
                    setResult(RESULT_OK, intent)
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    CustomCookieToast(context).showFailureToast(
                        data.title ?: "",
                        data.errorsList?.get(0)?.message ?: ""
                    )
                }
            }
        }
    }

    private fun initData() {
        toSpeechResponseModel = intent.getParcelableExtra("model")
        binding.tvTitle.text = toSpeechResponseModel?.documentName
        binding.tvDescription.text = toSpeechResponseModel?.text
        audioUrl = RetrofitBuilder.MEDIA_AUDIO_URL + toSpeechResponseModel?.audioPath

        token = SessionAndCookies.getUserToken(context) ?: ""

    }

    private fun preloadAudioAndWaveform() {
        binding.imgViewPlayAudio.isEnabled = false
        binding.topLayoutLoader.layoutLoader.visibility =
            View.VISIBLE // Make sure you have this in layout

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val amplituda = Amplituda(this@TextToSpeechDetailsActivity)
                val result = amplituda.processAudio(audioUrl!!).get()
                val samples = result.amplitudesAsList()

                withContext(Dispatchers.Main) {
                    binding.waveFormSeekBar.setSampleFrom(samples.toIntArray())
                    isWaveformReady = true
                    checkReadyState()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
            mediaPlayer = MediaPlayer().apply {
                setAudioStreamType(AudioManager.STREAM_MUSIC)
                setDataSource(audioUrl)
                prepareAsync()
                setOnPreparedListener {
                    isAudioReady = true
                    checkReadyState()
                }
                setOnCompletionListener {
                    stopAudio()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun checkReadyState() {
        if (isAudioReady && isWaveformReady) {
            binding.imgViewPlayAudio.isEnabled = true
            binding.topLayoutLoader.layoutLoader.visibility = View.GONE
        }
    }

    private fun setOnClickListeners() {
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.imgViewPlayAudio.setOnClickListener {
            if (isPlaying) pauseAudio() else startAudio()
        }

        binding.imgViewDelete.setOnClickListener {
            showDeletePopup()
        }

        binding.imgViewDownload.setOnClickListener {
            val name = "MagicSpeech_${System.currentTimeMillis()}.mp3"
            val audioPath = RetrofitBuilder.MEDIA_AUDIO_URL + toSpeechResponseModel?.audioPath
            binding.imgViewDownload.isEnabled = true

            checkPermissionAndDownload(audioPath, name)
        }
    }

    private fun startAudio() {
        mediaPlayer?.start()
        isPlaying = true
        binding.imgViewPlayAudio.setImageResource(R.drawable.audio_pause_ic)
        handler.post(updateWaveformRunnable)
    }

    private fun pauseAudio() {
        mediaPlayer?.pause()
        isPlaying = false
        binding.imgViewPlayAudio.setImageResource(R.drawable.audio_play_ic)
        handler.removeCallbacks(updateWaveformRunnable)
    }

    private fun stopAudio() {
        mediaPlayer?.pause()
        mediaPlayer?.seekTo(0)
        isPlaying = false
        binding.imgViewPlayAudio.setImageResource(R.drawable.audio_play_ic)
        binding.waveFormSeekBar.progress = 0f
        handler.removeCallbacks(updateWaveformRunnable)
    }

    private val updateWaveformRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    val progress = (it.currentPosition.toFloat() / it.duration * 100)
                    binding.waveFormSeekBar.progress = progress
                    handler.postDelayed(this, 100)
                }
            }
        }
    }

    private fun showDeletePopup() {
        val dialogBinding = ItemDeleteTextToSpeechPopupBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this, R.style.CustomDialog)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation

        dialogBinding.txtCancel.setOnClickListener { dialog.dismiss() }

        dialogBinding.txtYesDelete.setOnClickListener {
            dialog.dismiss()

            binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE

            textToSpeechId = toSpeechResponseModel?.documentId.toString()

            mainViewModel.callDeleteTextToSpeechListApi(
                context,
                token,
                textToSpeechId,
                binding.topLayoutLoader.layoutLoader
            )

        }

        dialog.show()
    }

    private fun downloadFile(url: String?, fileName: String?) {

        Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDescription("Downloading KEDVICK Speech")
        request.setTitle(fileName)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName)

        val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

    private fun checkPermissionAndDownload(audioPath: String, name: String) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            downloadFile(audioPath, name)
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // User denied permission normally (without "Don't ask again") -> request permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1001
                )
            } else {
                // User denied with "Don't ask again" -> show dialog to open settings
                showPermissionDialog()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted, please press download again.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Helper function to show a dialog
    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Storage permission is permanently denied. Please enable it from Settings to continue.")
            .setPositiveButton("Open Settings") { dialog, _ ->
                openAppSettings()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    // Helper function to open app settings
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }



    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(updateWaveformRunnable)
    }
}
