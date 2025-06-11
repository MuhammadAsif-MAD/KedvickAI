package com.kedvik.ai.ui.helpAndSupport.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.kedvik.ai.R
import com.kedvik.ai.databinding.ActivitySupportDetailBinding
import com.kedvik.ai.ui.auth.activities.BaseActivity
import com.kedvik.ai.ui.helpAndSupport.adapters.AdapterChat
import com.kedvik.ai.ui.helpAndSupport.models.ItemSupportReq
import com.kedvik.ai.ui.helpAndSupport.models.SupportDetails
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.FileUtils
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.utils.Utilities
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@Suppress("DEPRECATION")
class SupportDetailActivity : BaseActivity() {

    lateinit var myId : String
    lateinit var ticketId : String
    private var adapterChat: AdapterChat? = null
    private var imagePath: String = ""

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    val androidPermissionAbove13 = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    private val androidPermissionForOld = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

    private lateinit var requestGalleryPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var gallerySettingsIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionDialog: AlertDialog

    lateinit var binding: ActivitySupportDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportDetailBinding.inflate(layoutInflater)
        setContentView(binding.getRoot())

        initData()
        setUpUI()
        callSupportDetailApi()
        setObserver()
        initPermissionsResultLauncher()
    }

    private fun initData() {
        myId = SessionAndCookies.getMyId(context)?:""
        Utilities.setNavigationBarColor(context, R.color.bg_fields)
    }

    private fun callSupportDetailApi() {
        binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
        mainViewModel.callSupportDetailApi(context, ticketId, binding.layoutLoader.layoutLoader)
    }



    private fun setData(data: SupportDetails) {
        adapterChat?.setMessageList(data.message)
        binding.tvStatus.text = data.status
        if (data.status.equals("Open")) {
            binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.blue_600)))
        } else if (data.status.equals("Replied")) {
            binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.yellow_700)))
        } else if (data.status.equals("Pending")) {
            binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.pink_700)))
        } else if (data.status.equals("Resolved")) {
            binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.green_600)))
        } else if (data.status.equals("Closed")) {
            //Closed
            binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.grey_80)))
            binding.tvStatus.setAlpha(0.4f)
            binding.lvSendLayout.visibility = View.GONE
        }
        binding.recyclerView.smoothScrollToPosition(data.message.size)
    }

    private fun setUpUI() {
        val itemSupportReq: ItemSupportReq? = intent.getParcelableExtra("SUPPORT")
        ticketId = itemSupportReq?.ticketId?:""
        binding.tvTitle.text = itemSupportReq!!.subject
        binding.tvTicketID.text = itemSupportReq.ticketId
        binding.tvStatus.text = itemSupportReq.status
        when (itemSupportReq.status) {
            "Open" -> {
                binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.blue_600)))
            }
            "Replied" -> {
                binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.yellow_700)))
            }
            "Pending" -> {
                binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.pink_700)))
            }
            "Resolved" -> {
                binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.green_600)))
            }
            "Closed" -> {
                //Closed
                binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.grey_80)))
                binding.tvStatus.setAlpha(0.4f)
                binding.lvSendLayout.visibility = View.GONE
            }
        }
        when (itemSupportReq.priority) {
            "High" -> {
                binding.ivPriority.setImageTintList(ColorStateList.valueOf(getColor(R.color.red_700)))
            }
            "Normal" -> {
                binding.ivPriority.setImageTintList(ColorStateList.valueOf(getColor(R.color.cyan_500)))
            }
            "Low" -> {
                binding.ivPriority.setImageTintList(ColorStateList.valueOf(getColor(R.color.green_600)))
            }
        }
        adapterChat = AdapterChat(this)
        binding.recyclerView.setAdapter(adapterChat)
        binding.btnAttach.setOnClickListener {
            checkGalleryPermission()
        }
        binding.btnSend.setOnClickListener {
            if (binding.textContent.getText().toString().isEmpty()) {
                CustomCookieToast(context).showRequiredToast("Please Write Message")
            }else{
                startSend()
            }
        }

        binding.btClose.setOnClickListener { onBackPressedCallback.handleOnBackPressed() }
        binding.btRefresh.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btRefresh.setAlpha(0f)
            mainViewModel.callSupportDetailApi(context, ticketId, binding.layoutLoader.layoutLoader)
        }
    }


    // PickImageFromGallery
    private fun checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            } else {
                requestGalleryPermissionLauncher.launch(androidPermissionAbove13)
            }
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery()
            } else {
                requestGalleryPermissionLauncher.launch(androidPermissionForOld)
            }
        }
    }

    private fun initPermissionsResultLauncher() {
        requestGalleryPermissionLauncher = (this as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) {
                result: Map<String, Boolean> ->
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

        gallerySettingsIntentLauncher = (this as ComponentActivity).registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            checkGalleryPermission()
        }
    }

    private fun pickImageFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, 0)
    }

    @SuppressLint("SetTextI18n")
    private fun displayPermissionPopup() {
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.gallery_permissions_popup, null)
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constant.GALLERY_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                imagePath = FileUtils.getPath(context, selectedImage)?:""
                Utilities.loadImage(context, selectedImage.toString(), binding.btnAttach)
            }
        } catch (e: Exception) {
            CustomCookieToast(context).showFailureToast(e.toString())
        }
    }

    private fun createImageRequestBody(imagePath: String): MultipartBody.Part {
        val imageFile = File(imagePath)
        val requestFile = imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("attachment", imageFile.name, requestFile)
    }

    private fun startSend() {
        val message = binding.textContent.getText().toString()
        binding.btnSend.visibility = View.GONE
        binding.pbSend.visibility = View.VISIBLE

        val imageFilePath = File(imagePath)
        if (!TextUtils.isEmpty(imagePath)) {
            val requestImageName = imageFilePath.name.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestMyId = myId.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestMessage = message.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestTicketId = ticketId.toRequestBody("text/plain".toMediaTypeOrNull())

            val imageFile = createImageRequestBody(imagePath)
            mainViewModel.callSendToSupportImageMessageApi(context, requestImageName, imageFile, requestMyId, requestMessage, requestTicketId, binding.layoutLoader.layoutLoader)
        }else{
            mainViewModel.callSendToSupportTextMessageApi(context, myId, message, ticketId, binding.pbSend, binding.btnSend, binding.layoutLoader.layoutLoader)
        }
    }

    private fun setObserver() {
        mainViewModel.supportDetailsApi.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
                binding.btRefresh.setAlpha(1f)

                setData(data)
            }
        }
        mainViewModel.sendToSupportTextMessage.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.textContent.setText("")
                binding.btnSend.visibility = View.VISIBLE
                binding.pbSend.visibility = View.GONE
                adapterChat?.setMessageList(data.message)
                binding.recyclerView.smoothScrollToPosition(data.message.size)
            }
        }
        mainViewModel.sendToSupportImageMessage.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.textContent.setText("")
                imagePath = ""
                binding.btnSend.visibility = View.VISIBLE
                binding.pbSend.visibility = View.GONE
                binding.btnAttach.setImageResource(R.drawable.ic_attachment)

                adapterChat?.setMessageList(data.message)
                binding.recyclerView.smoothScrollToPosition(data.message.size)
            }
        }
    }

}