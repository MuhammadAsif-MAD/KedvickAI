package com.kedvik.ai.ui.researchCenter.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.Html
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvik.ai.ui.researchCenter.adapter.AdapterRobotChat
import com.kedvik.ai.ui.researchCenter.adapter.ChatBackgroundAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import com.kedvik.ai.R
import com.kedvik.ai.api.RetrofitBuilder
import com.kedvik.ai.databinding.ActivityChatBinding
import com.kedvik.ai.databinding.ItemBackgroundDialogBinding
import com.kedvik.ai.ui.auth.activities.BaseActivity
import com.kedvik.ai.ui.researchCenter.model.ChatBackgroundsModel
import com.kedvik.ai.ui.researchCenter.model.ChatDataItemModel
import com.kedvik.ai.utils.AnalyticsUtil
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.FileUtils
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.utils.Utilities
import com.kedvik.ai.utils.ViewAnimation
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Locale
import java.util.Objects

class ChatActivity : BaseActivity(), AdapterRobotChat.ItemClickedInterface,
    ChatBackgroundAdapter.ItemClickedInterface,
    PickiTCallbacks {
    private var modelList: MutableList<ChatDataItemModel> = arrayListOf()
    private var backgroundsList: MutableList<ChatBackgroundsModel> = arrayListOf()

    var query = ""
    var isDone = false
    private var myId: String = ""
    private var chatID = ""
    private var callFor = ""
    private var token = ""
    private var availableWords = ""
    private lateinit var dialog: Dialog
    private var selectedBgPosition: Int = -1
    private var imagePath: String = ""
    private lateinit var pickiT: PickiT
    var filePath: String = ""
    var pdfText: String = ""
    lateinit var attachmentType: String


    private val downloadPaths = arrayOf(
        "content://com.android.providers.downloads.documents",
        "content://com.android.externalstorage.documents"
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    val androidPermissionAbove13 = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    private val androidPermissionForOld = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var requestGalleryPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var gallerySettingsIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionDialog: AlertDialog
    private lateinit var chatBackgroundAdapter: ChatBackgroundAdapter
    private lateinit var adapterRoboChat: AdapterRobotChat
    lateinit var binding: ActivityChatBinding

    override fun onResume() {
        super.onResume()
        // Analytic event
        AnalyticsUtil.logEvent(context, "templates_screen", "Templates Screen")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@ChatActivity
        Utilities.setNavigationBarColor(context, R.color.bg_fields)

        initData()
        initLaunchers()
        initPermissionsResultLauncher()
        setClickListener()
        setUpUI()
        setObserver()

    }

    private fun initData() {
        myId = SessionAndCookies.getMyId(context) ?: ""
        callFor = intent.getStringExtra(Constant.CALL_FOR) ?: ""
        chatID = Utilities.generateChatID()
        token = SessionAndCookies.getUserToken(context) ?: ""
        availableWords = SessionAndCookies.getAvailableWord(context) ?: "0"
        //Initialize PickiT
        pickiT = PickiT(this, this, this)

        if (callFor == Constant.CHAT_HISTORY) {
//            binding.rlOptions.visibility = View.GONE
            binding.rlSendMessage.visibility = View.GONE

            chatID = intent.getStringExtra(Constant.CHAT_ID) ?: ""
            // Retrieve the JSON string from the intent extras
            val chatListJson = intent.getStringExtra(Constant.DATA_LIST)

            if (chatListJson != null) {
                val gson = Gson()
                val listType = object : TypeToken<MutableList<ChatDataItemModel>>() {}.type
                modelList = gson.fromJson(chatListJson, listType)
            }
        } else {
//            binding.rlOptions.visibility = View.VISIBLE
            binding.rlSendMessage.visibility = View.VISIBLE

            Utilities.setNavigationBarColor(context, R.color.bg_fields)
        }
        setAdapter()
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
//        binding.btnOption.setOnClickListener { view ->
//            showPopupMenu(view)
//        }
        binding.btnAttachment.setOnClickListener {
            showBottomSheet()
        }
        binding.rlAttachedImage.setOnClickListener {
            Constant.openImagePreviewActivity(context, binding.imgMessage, imagePath)
        }
        binding.rlAttachedPdf.setOnClickListener {
            Constant.startPdfViewerActivity(context, filePath, pdfText)
        }
        binding.btnRemoveAttachedPdf.setOnClickListener {
            showMedia(false)
        }
        binding.btnRemoveAttachedImage.setOnClickListener {
            imagePath = ""
            binding.rlAttachedImage.visibility = View.GONE
            binding.rlAttachment.visibility = View.VISIBLE

            hideUnHideSendButton()
        }
    }

    private fun setObserver() {
        mainViewModel.chatData.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.loadingSendMsg.visibility = View.GONE
                binding.ivSendMsg.visibility = View.VISIBLE
                hideUnHideSendButton()

                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE

                if (data.size == 0){
                    modelList[modelList.size - 1] = ChatDataItemModel("assistant", chatID, "⚠\uFE0F Insufficient Credit. You have exhausted the Words credit limit on your profile. Go to the Premium section to bundle!", myId)

                    // modelList.add(modelList.size - 1 , ChatDataItemModel("assistant", chatID, "", myId))

                    setAdapter()
                }else{
                    modelList = data

                    setAdapter()
                }
            }
        }
    }

    private fun setAdapter() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        adapterRoboChat = AdapterRobotChat(context, modelList, callFor, this)
        binding.recyclerView.adapter = adapterRoboChat

        binding.recyclerView.stopScroll()
        if (callFor == Constant.NEW_CHAT) {
            if (modelList.size > 0) {
                binding.recyclerView.smoothScrollToPosition(modelList.size)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpUI() {
        val drawables = listOf(
            R.drawable.bg_1, R.drawable.bg_2, R.drawable.bg_3, R.drawable.bg_4,
            R.drawable.bg_5, R.drawable.bg_6, R.drawable.bg_7, R.drawable.bg_8,
            R.drawable.bg_9, R.drawable.bg_10, R.drawable.bg_11, R.drawable.bg_12,
            R.drawable.bg_13, R.drawable.bg_14, R.drawable.bg_15, R.drawable.bg_16,
            R.drawable.bg_17, R.drawable.bg_18
        )
        
        backgroundsList = drawables.mapNotNull { drawableResId ->
            ContextCompat.getDrawable(this, drawableResId)?.let { drawable ->
                ChatBackgroundsModel(drawable, isSelected = false)
            }
        }.toMutableList()

        // Set initial background
        val savedBackgroundIndex = SessionAndCookies.getInt(this, Constant.BACKGROUND)
        if (savedBackgroundIndex < backgroundsList.size) {
            binding.ivBackground.setImageDrawable(backgroundsList[savedBackgroundIndex].drawable)
        }

        binding.btnSendMessage.setOnClickListener {
            SessionAndCookies.saveBoolean(context, Constant.IS_SERVICE_USED, true)

            if (availableWords == "") {
                CustomCookieToast(context).showFailureToast(
                    "Insufficient Credit",
                    "You have exhausted the Words credit limit on your profile. Go to the Premium section to bundle!"
                )
            } else {
                if (query == "" && TextUtils.isEmpty(imagePath) && TextUtils.isEmpty(filePath)) {
                    startSpeech()
                } else {
                    if (binding.edMessage.getText().toString().trim()
                            .isEmpty() && TextUtils.isEmpty(imagePath) && TextUtils.isEmpty(filePath)
                    ) {
                        CustomCookieToast(context).showRequiredToast("Please ask something")
                    } else {
                        binding.loadingSendMsg.visibility = View.VISIBLE
                        binding.ivSendMsg.visibility = View.GONE

                        startSend()
                    }
                }
            }
        }
        binding.edMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                val handler = Handler(Looper.getMainLooper())
                val runnable = Runnable {
                    if (modelList.size > 0) {
                        binding.recyclerView.smoothScrollToPosition(modelList.size)
                    }
                }
                handler.postDelayed(runnable, 200)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                query = s.trim().toString()
                if (s.trim().toString() == "" && TextUtils.isEmpty(imagePath) && TextUtils.isEmpty(
                        filePath
                    )
                ) {
                    isDone = false
                    binding.ivSendMsg.setImageResource(R.drawable.microphone_ic)
                    ViewAnimation.showScale(binding.btnSendMessage)
                } else {
                    if (!isDone) {
                        isDone = true
                        binding.ivSendMsg.setImageResource(R.drawable.send_message_ic)
                        ViewAnimation.showScale(binding.btnSendMessage)
                    }
                }
            }
        })
    }

    private fun startSpeech() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")
        try {
            someActivityResultLauncher.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(this, " " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startSend() {
        val message: String = binding.edMessage.getText().toString().trim()
        binding.edMessage.setText("")
        binding.btnSendMessage.setEnabled(false)

        val requestChatID = chatID.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestMessage = message.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestPdfText = pdfText.toRequestBody("text/plain".toMediaTypeOrNull())

        if (!TextUtils.isEmpty(message) && !TextUtils.isEmpty(imagePath)) {
            val imageFile = createImageRequestBody(imagePath)
            modelList.add(ChatDataItemModel("user", chatID, message, myId, imagePath))
            mainViewModel.callUserChatTextAndMediaMessageApi(
                context,
                token,
                requestChatID,
                requestMessage,
                imageFile,
                binding.layoutLoader.layoutLoader
            )
        } else if (!TextUtils.isEmpty(message) && !TextUtils.isEmpty(filePath)) {
            val pdfFile = createPdfRequestBody(filePath)
            modelList.add(ChatDataItemModel("user", chatID, message, myId, "", filePath))
            mainViewModel.callUserChatTextAndPdfMessageApi(
                context,
                token,
                requestChatID,
                requestMessage,
                pdfFile,
                requestPdfText,
                binding.layoutLoader.layoutLoader
            )
        } else if (!TextUtils.isEmpty(imagePath)) {
            val imageFile = createImageRequestBody(imagePath)
            modelList.add(ChatDataItemModel("user", chatID, message, myId, imagePath))
            mainViewModel.callUserChatMediaMessageApi(
                context,
                token,
                requestChatID,
                imageFile,
                binding.layoutLoader.layoutLoader
            )
        } else if (!TextUtils.isEmpty(filePath)) {
            val pdfFile = createPdfRequestBody(filePath)
            modelList.add(ChatDataItemModel("user", chatID, message, myId, "", filePath))
            mainViewModel.callUserChatPdfMessageApi(
                context,
                token,
                requestChatID,
                pdfFile,
                requestPdfText,
                binding.layoutLoader.layoutLoader
            )
        } else {
            modelList.add(ChatDataItemModel("user", chatID, message, myId))
            mainViewModel.callUserChatTextMessageApi(
                context,
                token,
                chatID,
                message,
                binding.layoutLoader.layoutLoader
            )
        }

        modelList.add(ChatDataItemModel("assistant", chatID, "LOADING", myId))
        adapterRoboChat.addMessages(modelList)

        showMedia(false)
        imagePath = ""
        binding.rlAttachedImage.visibility = View.GONE
        binding.rlAttachment.visibility = View.VISIBLE


        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            binding.btnSendMessage.setEnabled(true)
        }, 1000)
    }

    private fun createImageRequestBody(imagePath: String): MultipartBody.Part {
        val imageFile = File(imagePath)
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("media", imageFile.name, requestFile)
    }

    private fun createPdfRequestBody(imagePath: String): MultipartBody.Part {
        val imageFile = File(imagePath)
        val requestFile = imageFile.asRequestBody("*/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("pdf", imageFile.name, requestFile)
    }

    private fun startBackground() {
        dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding: ItemBackgroundDialogBinding =
            ItemBackgroundDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.getRoot())

        binding.rvBg.layoutManager =
            GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false)
        chatBackgroundAdapter = ChatBackgroundAdapter(context, backgroundsList, this)
        binding.rvBg.adapter = chatBackgroundAdapter

        if (dialog.window != null) {
            dialog.window!!.setAttributes(getLayoutParams(dialog))
            dialog.window!!.setBackgroundDrawableResource(R.color.transparent)
        }

        binding.btnClose.setOnClickListener {
            dialog.dismiss()
            // overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up)
        }
        binding.btnSave.setOnClickListener {
            if (selectedBgPosition != -1) {
                this.binding.ivBackground.setImageDrawable(backgroundsList[selectedBgPosition].drawable)
                SessionAndCookies.saveInt(
                    this@ChatActivity,
                    Constant.BACKGROUND,
                    selectedBgPosition
                )
                dialog.dismiss()
            }
        }
        dialog.show()
        // overridePendingTransition(R.anim.slide_out_up, R.anim.slide_bottom)
    }

    private fun getLayoutParams(dialog: Dialog): WindowManager.LayoutParams {
        val layoutParams = WindowManager.LayoutParams()
        if (dialog.window != null) {
            layoutParams.copyFrom(dialog.window!!.attributes)
        }
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        return layoutParams
    }

    private var someActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (result.data != null) {
                    val resultData =
                        result.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    binding.edMessage.setText(Objects.requireNonNull<ArrayList<String>?>(resultData)[0])
                    binding.edMessage.requestFocus()
                }
            }
        }

    override fun onItemClicked(position: Int, dataModel: ChatDataItemModel) {
        val selectedText = dataModel.text ?: ""
        val selectedImage = (RetrofitBuilder.MEDIA_URL + dataModel.media) ?: ""
        val selectedPdf = (RetrofitBuilder.MEDIA_URL + dataModel.pdf) ?: ""
        val isSender = dataModel.role != "assistant"

        val popup = PopupMenu(
            this, adapterRoboChat.getSelectedItemView(),
            if (isSender) {
                Gravity.END
            } else {
                Gravity.START
            },
            0,
            R.style.CustomPopupMenu
        )
        popup.inflate(R.menu.msg_menu)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popup.setForceShowIcon(true)
        }

        if (!TextUtils.isEmpty(dataModel.text) && !TextUtils.isEmpty(dataModel.media)) {

        } else if (!TextUtils.isEmpty(dataModel.text) && !TextUtils.isEmpty(dataModel.pdf)) {

        } else if (!TextUtils.isEmpty(dataModel.media)) {
            // Only Media
            popup.menu.findItem(R.id.menu_copy).isVisible = false
        } else if (!TextUtils.isEmpty(dataModel.pdf)) {
            // Only Pdf
            popup.menu.findItem(R.id.menu_copy).isVisible = false
        } else {
            // Only Text

        }

        popup.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.menu_copy) {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Copied", Html.fromHtml(selectedText).toString())
                clipboard.setPrimaryClip(clip)
            } else if (item.itemId == R.id.menu_share) {
                try {
                    if (!TextUtils.isEmpty(dataModel.text) && !TextUtils.isEmpty(dataModel.media)) {
                        Utilities.shareImageAndCaption(context, selectedImage, selectedText)
                    } else if (!TextUtils.isEmpty(dataModel.media)) {
                        Utilities.shareImageAndCaption(context, selectedImage, "")
                    } else if (!TextUtils.isEmpty(dataModel.text) && !TextUtils.isEmpty(dataModel.pdf)) {
                        Utilities.downloadAndSharePdf(context, selectedPdf, selectedText)
                    } else if (!TextUtils.isEmpty(dataModel.pdf)) {
                        Utilities.downloadAndSharePdf(context, selectedPdf, "")
                    } else {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.type = "text/plain"
                        var shareMessage = Html.fromHtml(selectedText).toString()
                        shareMessage =
                            "$shareMessage  *Download Now:* https://play.google.com/store/apps/details?id=$packageName\n\n"
                        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                        shareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                        startActivity(Intent.createChooser(shareIntent, "choose one"))
                    }
                } catch (e: java.lang.Exception) {
                    //e.toString();
                }
            }
            true
        }

        popup.show()
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view, Gravity.NO_GRAVITY, 0, R.style.CustomPopupMenu)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.toolbar_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.action_background -> {
                    startBackground()
                    true
                }

                R.id.action_history -> {
                    startActivity(Intent(this, ChatHistoryActivity::class.java))
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_background -> {
                startBackground()
                true
            }

            R.id.action_history -> {
                startActivity(Intent(this, ChatHistoryActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBackgroundItemClicked(position: Int) {
        selectedBgPosition = position

        for (i in 0 until backgroundsList.size) {
            backgroundsList[i].isSelected = (i == position)
            chatBackgroundAdapter.notifyDataSetChanged()
        }
    }

    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val sheetView: View = LayoutInflater.from(context)
            .inflate(R.layout.add_media_files_bottom_sheet, findViewById(R.id.bottom_sheet))
        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()
        bottomSheetDialog.dismissWithAnimation = true

        sheetView.findViewById<RelativeLayout>(R.id.rlAddImage).setOnClickListener {
            bottomSheetDialog.dismiss()
            attachmentType = Constant.IMAGE
            checkGalleryPermission(true)
        }

        sheetView.findViewById<RelativeLayout>(R.id.rlAddPdfFile).setOnClickListener {
            bottomSheetDialog.dismiss()
            attachmentType = Constant.PDF
            checkGalleryPermission(false)
        }

        sheetView.findViewById<RelativeLayout>(R.id.rlClose).setOnClickListener {
            attachmentType = Constant.TEXT
            bottomSheetDialog.dismiss()
        }
    }

    // PickImageFromGallery
    private fun checkGalleryPermission(isPickImage: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                if (isPickImage) {
                    pickImageFromGallery()
                } else {
                    openPdfPicker()
                }
            } else {
                requestGalleryPermissionLauncher.launch(androidPermissionAbove13)
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                if (isPickImage) {
                    pickImageFromGallery()
                } else {
                    openPdfPicker()
                }
            } else {
                requestGalleryPermissionLauncher.launch(androidPermissionForOld)
            }
        }
    }

    private fun initPermissionsResultLauncher() {
        requestGalleryPermissionLauncher =
            (this as ComponentActivity).registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: Map<String, Boolean> ->
                val permissions: Array<String> =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        androidPermissionAbove13
                    } else {
                        androidPermissionForOld
                    }
                var permissionGranted = false
                for (i in 0 until result.size) {
                    if (java.lang.Boolean.TRUE == result[permissions[i]]) {
                        permissionGranted = true
                    } else {
                        permissionGranted = false
                        break
                    }
                }
                if (permissionGranted) {
                    pickImageFromGallery()
                } else {
                    displayPermissionPopup()
                }
            }

        gallerySettingsIntentLauncher =
            (this as ComponentActivity).registerForActivityResult<Intent, ActivityResult>(
                ActivityResultContracts.StartActivityForResult()
            ) {
                checkGalleryPermission(true)
            }
    }

    private fun pickImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, 0)
    }

    @SuppressLint("SetTextI18n")
    private fun displayPermissionPopup() {
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View =
            LayoutInflater.from(context).inflate(R.layout.gallery_permissions_popup, null)
        builder.setView(customLayout)

        val btnSettings = customLayout.findViewById<TextView>(R.id.txtRight)
        val btnCancel = customLayout.findViewById<TextView>(R.id.txtLeft)

        permissionDialog = builder.create()
        permissionDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        permissionDialog.setCancelable(false)
        permissionDialog.show()
        btnSettings.setOnClickListener {
            permissionDialog.dismiss()

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            gallerySettingsIntentLauncher.launch(intent)
        }
        btnCancel.setOnClickListener { permissionDialog.dismiss() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constant.GALLERY_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, 0)
            } else {
                CustomCookieToast(context).showFailureToast(Constant.PERMISSION_DENIED)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == 0 && resultCode == RESULT_OK && null != data) {
                val selectedImage = data.data
                imagePath = FileUtils.getPath(context, selectedImage) ?: ""

                binding.rlAttachedImage.visibility = View.VISIBLE
                binding.rlAttachment.visibility = View.GONE
                Utilities.loadImage(context, selectedImage.toString(), binding.imgMessage)

                hideUnHideSendButton()
            }
        } catch (e: Exception) {
            CustomCookieToast(context).showFailureToast(e.toString())
        }
    }

    private fun hideUnHideSendButton() {
        if (binding.edMessage.text.toString().trim().isEmpty() && TextUtils.isEmpty(imagePath) && TextUtils.isEmpty(filePath)) {
            isDone = false
            binding.ivSendMsg.setImageResource(R.drawable.microphone_ic)
            ViewAnimation.showScale(binding.btnSendMessage)
        } else {
            if (!isDone) {
                isDone = true
                binding.ivSendMsg.setImageResource(R.drawable.send_message_ic)
                ViewAnimation.showScale(binding.btnSendMessage)
            }
        }
    }


    // PickPdfFromGallery
    private fun initLaunchers() {
        requestGalleryPermissionLauncher =
            (this as ComponentActivity).registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
            { result: Map<String, Boolean> ->
                val permissions: Array<String> =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        androidPermissionAbove13
                    } else {
                        androidPermissionForOld
                    }
                var permissionGranted = false
                for (i in 0 until result.size) {
                    if (java.lang.Boolean.TRUE == result[permissions[i]]) {
                        permissionGranted = true
                    } else {
                        permissionGranted = false
                        break
                    }
                }
                if (permissionGranted) {
                    openPdfPicker()
                } else {
                    buildPermissionAlert()
                }
            }

        gallerySettingsIntentLauncher =
            (this as ComponentActivity).registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                checkGalleryPermission(
                    false
                )
            }
    }


    private fun openPdfPicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(
            Intent.EXTRA_MIME_TYPES,
            arrayOf(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            )
        )
        pickPdfLauncher.launch(intent)

        /*val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/pdf"
        pickPdfLauncher.launch(intent)*/
    }

    private var pickPdfLauncher: ActivityResultLauncher<Intent> =
        (this as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult> { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val selectedUri = result.data!!.data

                    Log.i(Constant.TAG, "Uri : " + selectedUri.toString())
                    // check if the uri is a downloads or external storage uri,
                    // then save the content of uri into app's private storage
                    if (selectedUri.toString()
                            .contains(downloadPaths[0]) || selectedUri.toString()
                            .contains(downloadPaths[1])
                    ) {
                        try {
                            // converting uri into input stream
                            val inputStream = contentResolver.openInputStream(selectedUri!!)
                            // saving the stream into app's private storage
                            val file: File = saveInputStreamToFile(context, inputStream!!)!!
                            if (file == null) {
                                Toast.makeText(
                                    context,
                                    "streamed file is null!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@ActivityResultCallback
                            }
                            filePath = file.absolutePath

                            // show
                            showMedia(true)
                            Log.i(Constant.TAG, "Path : $filePath")
                        } catch (e: FileNotFoundException) {
                            val text = "File not found : " + e.localizedMessage
                            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
                            Log.e(Constant.TAG, "Exception : " + e.localizedMessage)
                            throw RuntimeException(e)
                        }
                    } else {
                        if (selectedUri != null && pickiT != null) {
                            pickiT.getPath(selectedUri, Build.VERSION.SDK_INT)
                        }
                    }
                }
            })


    @SuppressLint("SetTextI18n")
    private fun buildPermissionAlert() {
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View =
            LayoutInflater.from(context).inflate(R.layout.permissions_alert, null)
        builder.setView(customLayout)
        val label = customLayout.findViewById<TextView>(R.id.txtViewLabel)
        label.text = "Permission Required!"
        val message = customLayout.findViewById<TextView>(R.id.txtViewMessage)
        message.text =
            "Permission are required to access the media on this device, Go to setting and allow the Media permission for kedvik."
        val btnSettings = customLayout.findViewById<TextView>(R.id.btnRight)
        btnSettings.text = "Settings"
        val btnCancel = customLayout.findViewById<TextView>(R.id.btnLeft)
        btnCancel.text = "Cancel"
        permissionDialog = builder.create()
        permissionDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        permissionDialog.setCancelable(false)
        permissionDialog.show()
        btnSettings.setOnClickListener {
            permissionDialog.dismiss()

            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            gallerySettingsIntentLauncher.launch(intent)
        }
        btnCancel.setOnClickListener { permissionDialog.dismiss() }
    }

    // PDF Work
    override fun PickiTonUriReturned() {}

    override fun PickiTonStartListener() {
        Log.d(
            Constant.TAG,
            "-------------- Started ---------------"
        )
    }

    override fun PickiTonProgressUpdate(progress: Int) {
        //        Log.d(TAG, "on progress : " + progress);
    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        Log.d(Constant.TAG, "[------------------- Completed --------------------]")

        filePath = path ?: ""

        showMedia(true)
        Log.i(
            Constant.TAG,
            "Path : $filePath"
        )
    }

    override fun PickiTonMultipleCompleteListener(
        paths: java.util.ArrayList<String>?,
        wasSuccessful: Boolean,
        Reason: String?
    ) {

    }

    private fun extractDocumentText(filePath: String) {
        when {
            filePath.endsWith(".pdf", ignoreCase = true) -> {
                extractPdfText(filePath)
            }

            filePath.endsWith(".doc", ignoreCase = true) -> {
                extractDocText(filePath)
            }

            filePath.endsWith(".docx", ignoreCase = true) -> {
                extractDocxText(filePath)
            }

            else -> {
                Log.e(Constant.TAG, "Unsupported file type")
            }
        }
    }

    private fun extractPdfText(filePath: String) {
        try {
            val reader = PdfReader(filePath)
            val totalPageCount = reader.numberOfPages
            val textBuilder = StringBuilder()
            if (totalPageCount <= 4) {
                binding.rlAttachment.visibility = View.GONE
                binding.rlAttachedPdf.visibility = View.VISIBLE
                Log.d(Constant.TAG, totalPageCount.toString())
                for (i in 1..totalPageCount) {
                    val pageText = PdfTextExtractor.getTextFromPage(reader, i)
                    textBuilder.append(pageText).append("\n")

                    Log.d(Constant.TAG, "Loop$pageText")
                }

                reader.close()

                val extractedText = textBuilder.toString()
                this.pdfText = extractedText
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                Log.d(Constant.TAG, "Extracted Text $extractedText")
                // Display the extracted text
                runOnUiThread {
                    // Toast.makeText(this, "Extracted Text: $extractedText", Toast.LENGTH_LONG).show()
                    Log.i("TAG", "Extracted Text: $extractedText")
                }
            } else {
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                CustomCookieToast(context).showFailureToast("You can add maximum 4 pages PDF.")
                showMedia(false)
                Log.d(Constant.TAG, "Else condition")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            binding.layoutLoader.layoutLoader.visibility = View.GONE
            Log.d(Constant.TAG, "Exception + ${e.message}")
            runOnUiThread {
                Toast.makeText(this,
                    "Error extracting text: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun extractDocText(filePath: String) {
        try {
            val fileInputStream = FileInputStream(filePath)
            val document = HWPFDocument(fileInputStream)
            val extractor = WordExtractor(document)
            val text = extractor.text
            fileInputStream.close()

            this.pdfText = text
            binding.rlAttachment.visibility = View.GONE
            binding.rlAttachedPdf.visibility = View.VISIBLE
            binding.layoutLoader.layoutLoader.visibility = View.GONE
            Log.d(Constant.TAG, "Extracted Text $text")
            runOnUiThread {
                Log.i("TAG", "Extracted Text: $text")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showMedia(false)
            binding.layoutLoader.layoutLoader.visibility = View.GONE
            Log.d(Constant.TAG, "Exception + ${e.message}")
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Error extracting text: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun extractDocxText(filePath: String) {
        try {
            val fileInputStream = FileInputStream(filePath)
            val document = XWPFDocument(fileInputStream)
            val textBuilder = StringBuilder()
            document.paragraphs.forEach { paragraph ->
                textBuilder.append(paragraph.text).append("\n")
            }
            fileInputStream.close()

            val extractedText = textBuilder.toString()
            this.pdfText = extractedText

            binding.rlAttachment.visibility = View.GONE
            binding.rlAttachedPdf.visibility = View.VISIBLE
            binding.layoutLoader.layoutLoader.visibility = View.GONE
            Log.d(Constant.TAG, "Extracted Text $extractedText")
            runOnUiThread {
                Log.i("TAG", "Extracted Text: $extractedText")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            showMedia(false)
            binding.layoutLoader.layoutLoader.visibility = View.GONE
            Log.d(Constant.TAG, "Exception + ${e.message}")
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Error extracting text: ${e.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun showMedia(show: Boolean) {
        if (show) {
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            extractDocumentText(filePath)
        } else {
            binding.rlAttachment.visibility = View.VISIBLE
            binding.rlAttachedPdf.visibility = View.GONE
            filePath = ""
            pdfText = ""
        }
        hideUnHideSendButton()
    }

    private fun saveInputStreamToFile(context: Context, inputStream: InputStream): File? {
        return try {
            // Get a reference to the file where you want to save the PDF
            val outputFile = File(context.filesDir, "temp_pdf.pdf")

            // Open a FileOutputStream for the output file
            val outputStream = FileOutputStream(outputFile)

            // Read the contents of the InputStream into a byte array
            val buffer = ByteArray(4096)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            // Close the InputStream and FileOutputStream
            inputStream.close()
            outputStream.close()
            outputFile
        } catch (e: java.lang.Exception) {
            null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isChangingConfigurations) {
            pickiT.deleteTemporaryFile(this)
        }
    }

}