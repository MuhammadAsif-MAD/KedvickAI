package com.kedvik.ai.ui.settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.kedvik.ai.R
import com.kedvik.ai.databinding.ActivityEditProfileBinding
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

class EditProfileActivity : BaseActivity() {
    var name: String = ""
    var email: String = ""
    private var myId: String = ""
    private var imagePath: String = ""

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    val androidPermissionAbove13 = arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    private val androidPermissionForOld = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val googlePixelGalleryBelowPermissions =
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    private val galleryRequestCode = 0

    var done: Boolean = false
    private lateinit var requestGalleryPermissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var gallerySettingsIntentLauncher: ActivityResultLauncher<Intent>
    lateinit var permissionDialog: AlertDialog
    private lateinit var binding: ActivityEditProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@EditProfileActivity

        initData()
        initPermissionsResultLauncher()
        editTextWatcher()
        setClickListener()
        setObserver()
    }

    private fun initData() {
        myId = SessionAndCookies.getMyId(context) ?: ""
        imagePath = SessionAndCookies.getProfileImage(context) ?: ""
        name = SessionAndCookies.getUserName(context) ?: ""
        email = SessionAndCookies.getUserEmail(context) ?: ""

        if (!TextUtils.isEmpty(imagePath)) {
            Utilities.loadImage(context, imagePath, binding.ivUserImage)
        } else {
            binding.ivUserImage.setImageResource(R.drawable.place_holder)
        }
        binding.edName.setText(name)
        binding.edEmail.setText(email)
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }
        binding.rlProfileImage.setOnClickListener {
            Constant.openImagePreviewActivity(context, binding.ivUserImage, imagePath)
        }
        binding.rlChangeProfile.setOnClickListener {
            if (TextUtils.isEmpty(imagePath)) {
                checkGalleryPermission()
            } else {
                showBottomSheet()
            }
        }
        binding.btnSaveChanges.setOnClickListener {
            checkValidations()
        }
    }

    private fun editTextWatcher() {
        binding.edName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!done) {
                    val validUsername = getValidUserName(s.toString())
                    binding.edName.setText(validUsername)
                    binding.edName.setSelection(validUsername.length)
                } else {
                    done = false
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.edEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val text: String = binding.edEmail.text.toString()
                if (text.startsWith(" ")) {
                    binding.edEmail.setText(text.trim())
                    if (!TextUtils.isEmpty(text.trim())) {
                        binding.edEmail.setSelection(text.trim().length)
                    }
                }
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })
    }

    private fun checkValidations() {
        name = binding.edName.text.toString().trim()
        email = binding.edEmail.text.toString().trim()
        binding.edEmail.requestFocus()

        if (TextUtils.isEmpty(name)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterYourName))
        } else if (TextUtils.isEmpty(email)) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterYourEmail))
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            CustomCookieToast(context).showRequiredToast(resources.getString(R.string.PleaseEnterValidEmail))
        } else {
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            // Api Call
            if (!TextUtils.isEmpty(imagePath) && !imagePath.contains("/uploads")) {
                val requestMyId = myId.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestName = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val requestEmail = email.toRequestBody("text/plain".toMediaTypeOrNull())

                val imageFile = createImageRequestBody(imagePath)
                mainViewModel.callProfileImageUpdateApi(
                    context,
                    requestMyId,
                    requestName,
                    requestEmail,
                    imageFile,
                    binding.layoutLoader.layoutLoader
                )
            } else {
                mainViewModel.callProfileUpdateApi(
                    context,
                    myId,
                    name,
                    email,
                    binding.layoutLoader.layoutLoader
                )
            }
        }
    }

    private fun createImageRequestBody(imagePath: String): MultipartBody.Part {
        val imageFile = File(imagePath)
        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("image", imageFile.name, requestFile)
    }

    private fun callUserProfileApi() {
        mainViewModel.callUserProfileApi(context, myId, binding.layoutLoader.layoutLoader)
    }

    private fun setObserver() {
        mainViewModel.profileUpdate.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                SessionAndCookies.saveUserName(context, name)
                SessionAndCookies.saveUserEmail(context, email)
                binding.layoutLoader.layoutLoader.visibility = View.GONE

                CustomCookieToast(context).showSuccessToast("Profile updated successfully")
            }
        }
        mainViewModel.profileImageUpdate.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->

                callUserProfileApi()
                val userImage = data.data?.image ?: ""
                SessionAndCookies.saveUserName(context, name)
                SessionAndCookies.saveUserEmail(context, email)
                SessionAndCookies.saveProfileImage(context, userImage)
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                CustomCookieToast(context).showSuccessToast("Profile updated successfully")

            }
        }
        mainViewModel.profilePictureRemove.observe(this) { event ->
            event.getContentIfNotHandled()?.let { dataModel ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if (dataModel.status!!) {
                    // Save Session
                    SessionAndCookies.saveProfileImage(context, "")
                    CustomCookieToast(context).showSuccessToast("Profile updated successfully")

                    if (dataModel.status) {
                        imagePath = ""
                        binding.ivUserImage.setImageResource(R.drawable.place_holder)
                    }
                } else {
                    CustomCookieToast(context).showFailureToast(dataModel.action!!)
                }
            }
        }
        mainViewModel.userProfile.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                SessionAndCookies.saveUserProfileData(context, data)

                imagePath = data.image ?: ""
            }
        }
    }

    private fun getValidUserName(name: String): String {
        var name = name
        done = true
        for (i in name.indices) {
            if (name.length > i) {
                val ch = name[i]
                if (i == 0) {
                    if ((ch < 'A' || ch > 'Z') && (ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '_' && ch != '.') {
                        name = name.substring(0, i) + name.substring(i + 1)
                    }
                } else if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '_' && ch != '.') {
                    name = name.substring(0, i) + name.substring(i + 1)
                }
            }
        }
        return name
    }

    // Profile Image Update
    private fun showBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(context, R.style.BottomSheetTheme)
        val sheetView: View = LayoutInflater.from(context)
            .inflate(R.layout.bottom_sheet_4_options, findViewById(R.id.bottom_sheet))
        bottomSheetDialog.setContentView(sheetView)
        bottomSheetDialog.show()
        bottomSheetDialog.dismissWithAnimation = true

        sheetView.findViewById<View>(R.id.rlOne).setOnClickListener {
            bottomSheetDialog.dismiss()
            Constant.openImagePreviewActivity(context, binding.ivUserImage, imagePath)
        }
        sheetView.findViewById<View>(R.id.rlTwo).setOnClickListener {
            bottomSheetDialog.dismiss()
            checkGalleryPermission()
        }
        sheetView.findViewById<View>(R.id.rlThree).setOnClickListener {
            bottomSheetDialog.dismiss()
            if (imagePath.contains("/upload")) {
                alertRemoveProfile()
            } else {
                imagePath = ""
                binding.ivUserImage.setImageResource(R.drawable.place_holder)
            }
        }
        sheetView.findViewById<View>(R.id.rlCancel).setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun alertRemoveProfile() {
        val alertDialog: AlertDialog
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.item_popup, null)
        builder.setView(customLayout)
        val tvAlert = customLayout.findViewById<TextView>(R.id.tv_alert)
        val tvAlertDescription = customLayout.findViewById<TextView>(R.id.tv_alert_desc)
        val txtYes = customLayout.findViewById<TextView>(R.id.txtYes)
        val txtNo = customLayout.findViewById<TextView>(R.id.txtNo)
        tvAlert.text = "Remove Profile!"
        tvAlertDescription.text = "Are you sure you want to remove \nthis profile picture?"
        txtYes.text = "Confirm"
        txtYes.setTextColor(ContextCompat.getColor(context, R.color.dark_red_color))
        alertDialog = builder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.show()
        alertDialog.setCancelable(false)
        txtYes.setOnClickListener {
            alertDialog.dismiss()
            // Api Call
            Utilities.hideKeyboard(context)
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            mainViewModel.callProfilePictureRemoveApi(
                context,
                myId,
                binding.layoutLoader.layoutLoader
            )
        }
        txtNo.setOnClickListener { alertDialog.dismiss() }
    }

    // PickImageFromGallery
    private fun checkGalleryPermission() {
        val manufacturer = Build.MANUFACTURER
        if (manufacturer.equals("google", ignoreCase = true)) {
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
                    requestGalleryPermissionLauncher.launch(googlePixelGalleryBelowPermissions)
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    requestGalleryPermissionLauncher.launch(androidPermissionAbove13)
                }
            } else {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGallery()
                } else {
                    requestGalleryPermissionLauncher.launch(androidPermissionForOld)
                }
            }
        }
    }

    private fun initPermissionsResultLauncher() {
        requestGalleryPermissionLauncher =
            (this as ComponentActivity).registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result: Map<String, Boolean> ->
                val manufacturer = Build.MANUFACTURER
                val permissions: Array<String> =
                    if (manufacturer.equals("google", ignoreCase = true)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            androidPermissionAbove13
                        } else {
                            googlePixelGalleryBelowPermissions
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            androidPermissionAbove13
                        } else {
                            androidPermissionForOld
                        }
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

        gallerySettingsIntentLauncher = (this as ComponentActivity).registerForActivityResult<Intent, ActivityResult>(
                ActivityResultContracts.StartActivityForResult()) {
                checkGalleryPermission()
            }
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

    private fun pickImageFromGallery() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityIfNeeded(photoPickerIntent, galleryRequestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constant.GALLERY_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityIfNeeded(photoPickerIntent, galleryRequestCode)
            } else {
                CustomCookieToast(context).showFailureToast(Constant.PERMISSION_DENIED)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == galleryRequestCode && resultCode == RESULT_OK && null != data) {
                val selectedImage = data.data
                imagePath = FileUtils.getPath(context, selectedImage) ?: ""
                Utilities.loadImage(context, selectedImage.toString(), binding.ivUserImage)
            }
        } catch (e: Exception) {
            CustomCookieToast(context).showFailureToast(e.toString())
        }
    }
}