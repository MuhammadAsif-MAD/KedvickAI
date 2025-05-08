package com.kedvick.ai.ui.textToSpeech.activities

import android.app.DownloadManager
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.R
import com.kedvick.ai.ui.textToSpeech.adapters.ListOfAvailableSpeechAdapter
import com.kedvick.ai.ui.textToSpeech.model.TextToSpeechResponseModel
import com.kedvick.ai.api.RetrofitBuilder
import com.kedvick.ai.databinding.ActivityListOfAvailableSpeechBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.utils.AnalyticsUtil
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.SessionAndCookies
import java.io.IOException

class ListOfAvailableSpeechActivity : BaseActivity(),
    ListOfAvailableSpeechAdapter.ItemClickedInterface {

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var rvAdapter: ListOfAvailableSpeechAdapter
    private var myId: String = ""
    var modelList: MutableList<TextToSpeechResponseModel> = arrayListOf()
    private lateinit var binding: ActivityListOfAvailableSpeechBinding

    val detailsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if (it.resultCode == RESULT_OK) {
                val model = it.data?.getParcelableExtra<TextToSpeechResponseModel>("model")
                model?.let {
                    modelList.remove(it)
                    rvAdapter.notifyItemRemoved(it.adapterPosition)
                    rvAdapter.notifyItemRangeChanged(it.adapterPosition,modelList.size)

                }
            }
        }

    override fun onResume() {
        super.onResume()
        if (SessionAndCookies.getBoolean(context, Constant.IS_TEXT_TO_SPEECH_UPDATED)) {
            SessionAndCookies.saveBoolean(context, Constant.IS_TEXT_TO_SPEECH_UPDATED, false)
            binding.llNoSpeechFound.visibility = View.GONE
            callTextToSpeechApi(true)
        }
        // Analytic event
        AnalyticsUtil.logEvent(context, "text_to_speech_screen", "Text To Speech Screen")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOfAvailableSpeechBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@ListOfAvailableSpeechActivity

        initData()
        setClickListener()
        callTextToSpeechApi(true)
        setObserver()

    }

    private fun initData() {
        myId = SessionAndCookies.getMyId(context) ?: ""
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.rlTextToSpeech.setOnClickListener {
            Constant.startTextToSpeechActivity(context)
        }
        binding.rlTextToSpeechTwo.setOnClickListener {
            Constant.startTextToSpeechActivity(context)
        }
    }

    private fun callTextToSpeechApi(isLoaderShow: Boolean) {
        if (isLoaderShow) {
            binding.llShimmer.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            if (modelList.size == 0) {
                binding.llShimmer.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }
        mainViewModel.callTextToSpeechListApi(context, myId, binding.llShimmer)
    }

    private fun setAdapter() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvAdapter = ListOfAvailableSpeechAdapter(context, modelList, this)
        binding.recyclerView.adapter = rvAdapter
    }

    private fun setObserver() {
        mainViewModel.textToSpeechList.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.llShimmer.visibility = View.GONE
                modelList = data

                if (modelList.size == 0) {
                    binding.llNoSpeechFound.visibility = View.VISIBLE
                    binding.rlTextToSpeechTwo.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.llNoSpeechFound.visibility = View.GONE
                    binding.rlTextToSpeechTwo.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.VISIBLE
                }
                setAdapter()
            }
        }
    }

    override fun onArrowButtonClicked(dataModel: TextToSpeechResponseModel) {
        val uri = RetrofitBuilder.MEDIA_AUDIO_URL + dataModel.audioPath

        val intent = Intent(context, TextToSpeechDetailsActivity::class.java)
        intent.putExtra("model", dataModel)
        detailsLauncher.launch(intent)
        context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
    }


    //  override fun onPlayButtonClicked(dataModel: TextToSpeechResponseModel) {
    //   playAudio(RetrofitBuilder.MEDIA_AUDIO_URL + dataModel.audioPath)
//    }
//
//    override fun onPauseButtonClicked(dataModel: TextToSpeechResponseModel) {
//        pauseAudio()
//    }
//
//    override fun onDownloadButtonClicked(dataModel: TextToSpeechResponseModel) {
//        Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
//        val name = "MagicSpeech_" + System.currentTimeMillis() + ".mp3"
//        val audioPath = RetrofitBuilder.MEDIA_AUDIO_URL + dataModel.audioPath
//        downloadFile(audioPath, name)
//    }

    private fun playAudio(audioUrl: String) {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        } else {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }

        try {
            mediaPlayer?.setDataSource(audioUrl)
            mediaPlayer?.prepare()
            mediaPlayer?.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Toast.makeText(this, "Audio started playing..", Toast.LENGTH_SHORT).show()
    }

    private fun pauseAudio() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()

            binding.layoutLoader.layoutLoader.visibility = View.GONE
            Toast.makeText(this, "Audio has been paused", Toast.LENGTH_SHORT).show()
        } else {
            binding.layoutLoader.layoutLoader.visibility = View.GONE
            // Toast.makeText(this, "Audio has not played", Toast.LENGTH_SHORT).show()
        }
    }

    private fun downloadFile(url: String?, fileName: String?) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setDescription("Downloading KEDVICK Speech")
        request.setTitle(fileName)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, fileName)

        // get download service and enqueue file
        val manager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}