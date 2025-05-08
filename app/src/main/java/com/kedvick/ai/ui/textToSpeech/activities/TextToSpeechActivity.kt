package com.kedvick.ai.ui.textToSpeech.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jaredrummler.materialspinner.MaterialSpinner
import com.kedvick.ai.R
import com.kedvick.ai.api.RetrofitBuilder
import com.kedvick.ai.databinding.ActivityTextToSpeechBinding
import com.kedvick.ai.ui.auth.activities.BaseActivity
import com.kedvick.ai.ui.createImage.adapters.StyleSpinnerAdapter
import com.kedvick.ai.utils.Constant
import com.kedvick.ai.utils.CustomCookieToast
import com.kedvick.ai.utils.SessionAndCookies
import com.kedvick.ai.utils.Utilities
import java.io.IOException

class TextToSpeechActivity : BaseActivity() {

    private var languagesList = arrayOf("English", "Danish", "French", "Japanese", "Dutch", "Polish", "Portuguese", "Russian")
    private var countryFlagsList = intArrayOf(R.drawable.lg_en, R.drawable.lg_da, R.drawable.lg_fr, R.drawable.lg_ja, R.drawable.lg_nl, R.drawable.lg_pl, R.drawable.lg_pt, R.drawable.lg_ru)

    private val genderList = mutableListOf("Male", "Female")
    private var mediaPlayer: MediaPlayer? = null

    lateinit var myId: String
    private lateinit var availableWords: String
    private lateinit var fileName: String
    private lateinit var content: String
    private var gender = "Male"
    private var language = "English"
    private var fileCharCounts = 0
    private var contentCharCounts = 0
    private var audioOutputPath: String = ""

    private lateinit var binding: ActivityTextToSpeechBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTextToSpeechBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@TextToSpeechActivity


        initSpinner()
        initData()
        setTextWatcher()
        setClickListener()
        setObserver()

    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        myId = SessionAndCookies.getMyId(context)?:""
        availableWords = SessionAndCookies.getAvailableWord(context)?:""
        binding.mSpinnerGender.text = "Male"
        binding.mSpinnerGender.setTextColor(Color.WHITE)

        if (availableWords == "1"){
            binding.tvAvailableWordsCount.text = "$availableWords Word"
        }else{
            binding.tvAvailableWordsCount.text = "$availableWords Words"
        }
    }

    private fun initSpinner() {
        // Gender Spinner
        binding.mSpinnerGender.setItems(genderList)
        binding.mSpinnerGender.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener { view: MaterialSpinner?, position: Int, id: Long, item: String ->
            gender = item
        } as MaterialSpinner.OnItemSelectedListener<String>)

        // Language Spinner
        val languageSpinner = findViewById<View>(R.id.mSpinnerLanguage) as Spinner
        val languageSpinnerAdapter = StyleSpinnerAdapter(applicationContext, countryFlagsList, languagesList)
        languageSpinner.setAdapter(languageSpinnerAdapter)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                language = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun setTextWatcher() {
        binding.edFileName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {
                fileCharCounts = s.toString().length
                val maxChar = s.toString().length.toString()
                binding.tvNameCharCounts.text = "$maxChar/50"
            }
        })

        binding.edContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {
                contentCharCounts = s.toString().length
                val maxChar = s.toString().length.toString()
                binding.tvContentCharCount.text = "$maxChar/300"
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }

        binding.btnReset.setOnClickListener {
            binding.edFileName.setText("")
            binding.edContent.setText("")
            binding.tvNameCharCounts.text = "0/50"
            binding.tvContentCharCount.text = "0/300"
        }

        binding.btnPlay.setOnClickListener {
            playAudio(RetrofitBuilder.MEDIA_AUDIO_URL + audioOutputPath)
        }
        binding.btnPause.setOnClickListener {
            pauseAudio()
        }
        binding.btnDownload.setOnClickListener {
            val name = "MagicSpeech_${System.currentTimeMillis()}.mp3"
            val audioPath = RetrofitBuilder.MEDIA_AUDIO_URL + audioOutputPath

            checkPermissionAndDownload(audioPath, name)
        }

        binding.btnGenerate.setOnClickListener {
            startGenerating()
        }

    }

    private fun startGenerating() {
        SessionAndCookies.saveBoolean(context, Constant.IS_SERVICE_USED, true)
        fileName = binding.edFileName.text.toString().trim()
        content = binding.edContent.text.toString().trim()

        if (TextUtils.isEmpty(fileName)){
            CustomCookieToast(context).showRequiredToast("Please write file name")
        }else if (TextUtils.isEmpty(content)){
            CustomCookieToast(context).showRequiredToast("Please write content")
        }else if ((contentCharCounts) > Integer.parseInt(availableWords)){
            // CustomCookieToast(context).showFailureToast("You have insufficient words credit")
        }else{
            callCreateMagicArtApi()
        }
    }

    private fun callCreateMagicArtApi() {
        // Api Call
        Utilities.hideKeyboard(context)
        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
        mainViewModel.callCreateTextToSpeechApi(context, myId, fileName, content, gender, language, binding.layoutLoader.layoutLoader)
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        mainViewModel.createTextToSpeech.observe(this)  { event ->
            event.getContentIfNotHandled()?.let { dataModel ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                val computedValue = contentCharCounts - Integer.parseInt(availableWords)
                availableWords = Math.abs(computedValue).toString()

                if (availableWords == "1"){
                    binding.tvAvailableWordsCount.text = "$availableWords Word"
                }else{
                    binding.tvAvailableWordsCount.text = "$availableWords Words"
                }

                audioOutputPath = dataModel.audioPath?:""

                binding.llPlayPause.visibility = View.VISIBLE
                SessionAndCookies.saveAvailableWords(context, availableWords)
                SessionAndCookies.saveBoolean(context, Constant.IS_TEXT_TO_SPEECH_UPDATED, true)
                displayTextToSpeechGeneratedPopup()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayTextToSpeechGeneratedPopup() {
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.image_generated_popup, null)
        builder.setView(customLayout)

        val tvTitle = customLayout.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = customLayout.findViewById<TextView>(R.id.tvDescription)
        val txtClose = customLayout.findViewById<TextView>(R.id.txtClose)

        tvTitle.text = "Text To Speech File Generated!"
        tvDescription.text = "Congratulations! You have created your text\nto speech file using our AI."

        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.setCancelable(false)
        alertDialog.show()
        txtClose.setOnClickListener {
            alertDialog.dismiss()

            // onBackPressedCallback.handleOnBackPressed()
        }
    }

    private fun playAudio(audioUrl: String) {
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }else{
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

    }
    private fun pauseAudio() {
        if (mediaPlayer != null && mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()

            // Toast.makeText(this, "Audio has been paused", Toast.LENGTH_SHORT).show()
        } else {
            // Toast.makeText(this, "Audio has not played", Toast.LENGTH_SHORT).show()
        }
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


    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}