package com.kedvik.ai.ui.helpAndSupport.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.jaredrummler.materialspinner.MaterialSpinner
import com.kedvik.ai.R
import com.kedvik.ai.databinding.ActivityCreateSupportBinding
import com.kedvik.ai.ui.auth.activities.BaseActivity
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
class CreateSupportActivity : BaseActivity() {

    private val generalInquiryList = mutableListOf("General Inquiry", "Technical Issue", "Improvement Idea", "Feedback")
    private val priorityList = mutableListOf("Low", "Medium", "High")

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    val androidPermissionAbove13 = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    private val androidPermissionForOld = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var requestGalleryPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var gallerySettingsIntentLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionDialog: AlertDialog

    private var generalInquiry: String = ""
    private var priority: String = "Low"
    private var myId: String = ""
    private var imagePath: String = ""

    private lateinit var binding: ActivityCreateSupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@CreateSupportActivity

        initData()
        setClickListener()
        initPermissionsResultLauncher()
        setObserver()
    }

    private fun initData() {
        myId = SessionAndCookies.getMyId(context)?:""
        Constant.setWindowToAdjustResize(context)
    }


    private fun setClickListener() {
        binding.btnBack.setOnClickListener { onBackPressedCallback.handleOnBackPressed() }

        binding.mSpinnerGeneralInquiry.setItems(generalInquiryList)
        binding.mSpinnerPriority.setItems(priorityList)

        binding.mSpinnerGeneralInquiry.setOnClickListener {
            Utilities.hideKeyboard(context)
        }
        binding.mSpinnerPriority.setOnClickListener {
            Utilities.hideKeyboard(context)
        }
        binding.mSpinnerGeneralInquiry.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener { _: MaterialSpinner?, _: Int, _: Long, item: String ->
            generalInquiry = item
        } as MaterialSpinner.OnItemSelectedListener<String>)

        binding.mSpinnerPriority.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener { _: MaterialSpinner?, _: Int, _: Long, item: String ->
            priority = item
        } as MaterialSpinner.OnItemSelectedListener<String>)


        binding.btnCreateRequest.setOnClickListener {
            if (validate()) {
                sendRequest()
            }
        }
        binding.lvAdd.setOnClickListener {
            pickImageFromGallery()
        }
    }

    private fun validate(): Boolean {
        if (binding.edSubjectName.getText().toString().isEmpty()) {
            CustomCookieToast(context).showRequiredToast("Document name is required")
            return false
        } else if (binding.edMessage.getText().toString().isEmpty()) {
            CustomCookieToast(context).showRequiredToast("Instruction is required")
            return false
        }
        return true
    }


    private fun sendRequest() {
        val subject: String = binding.edSubjectName.getText().toString().trim()
        val message: String = binding.edMessage.getText().toString().trim()

        Utilities.hideKeyboard(context)
        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
        // Api Call
        if (!TextUtils.isEmpty(imagePath) && !imagePath.contains("/uploads")) {
            val tempImageFile = File(imagePath)
            val requestImageName = tempImageFile.name.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestMyId = myId.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestSubject = subject.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestCategory = generalInquiry.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestPriority = priority.toRequestBody("text/plain".toMediaTypeOrNull())
            val requestMessage = message.toRequestBody("text/plain".toMediaTypeOrNull())

            val imageFile = createImageRequestBody(imagePath)
            mainViewModel.callCreateSupportApi(context, requestImageName, imageFile, requestMyId, requestSubject, requestCategory, requestPriority, requestMessage, binding.layoutLoader.layoutLoader)
        }else{
            mainViewModel.callCreateSupportApi(context, myId, subject, generalInquiry, priority, message, binding.layoutLoader.layoutLoader)
        }
    }

    private fun setObserver() {
        mainViewModel.createSupportRequest.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                binding.layoutLoader.layoutLoader.visibility = View.GONE

                CustomCookieToast(context).showSuccessToast("Your Support Request has been sent.")

                Handler(Looper.myLooper()!!).postDelayed({
                    onBackPressedCallback.handleOnBackPressed()
                }, 1500)
            }
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
                Utilities.loadImage(context, selectedImage.toString(), binding.ivImage)
            }
        } catch (e: Exception) {
            CustomCookieToast(context).showFailureToast(e.toString())
        }
    }

    private fun createImageRequestBody(imagePath: String): MultipartBody.Part {
        val imageFile = File(imagePath)
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
    }
}