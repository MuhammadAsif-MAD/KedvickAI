package com.kedvik.ai.ui.createImage.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import com.jsibbold.zoomage.ZoomageView
import com.kedvik.ai.R
import com.kedvik.ai.databinding.ActivityArtImagePreviewBinding
import com.kedvik.ai.ui.auth.activities.BaseActivity
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.SessionAndCookies
import jp.wasabeef.glide.transformations.BlurTransformation
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.Calendar

class ArtImagePreviewActivity : BaseActivity() {

    private lateinit var blurImage: ImageView
    private lateinit var imageDetailed: ZoomageView
    lateinit var image: String
    private lateinit var artId: String

    private lateinit var binding: ActivityArtImagePreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArtImagePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@ArtImagePreviewActivity

        initData()
        setClickListener()
        setObserver()
    }

    private fun initData() {
        image = intent.getStringExtra("image")?:""
        artId = intent.getStringExtra("art_id")?:""

        imageDetailed = findViewById(R.id.image_Detailed)
        blurImage = findViewById(R.id.blurImage)

        Glide.with(context).load(image)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(75, 3)))
            .diskCacheStrategy(DiskCacheStrategy.DATA).signature(
                MediaStoreSignature(
                    "*/*",
                    Calendar.DATE.toLong(), 0
                )
            ).into(blurImage)


        if (image.contains("/uploads")) {
            Glide.with(context) // .setDefaultRequestOptions(new RequestOptions().error(R.drawable.img_avatar_cover))
                .load(image)
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable?>,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable?>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.imgLoader.visibility = View.GONE
                        return false
                    }
                }).into(imageDetailed)
        } else {
            Glide.with(context).load(image).diskCacheStrategy(DiskCacheStrategy.DATA)
                .error(R.drawable.place_holder)
                .placeholder(R.drawable.place_holder).into(imageDetailed)
        }
    }

    private fun setClickListener() {
        binding.btnBack.setOnClickListener { onBackPressedCallback.handleOnBackPressed()}
        binding.btnDownloadImage.setOnClickListener {
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE

            val name = "MagicArt_" + System.currentTimeMillis()
            val loadLogo = LoadDownload(context, image, getFilePath().absolutePath, name, binding.layoutLoader.layoutLoader, onImageGenerated = {displayImageGeneratedPopup()})
            loadLogo.execute()
        }
        binding.btnDeleteImage.setOnClickListener{
            displayDeleteImagePopup(context, artId)
        }
    }

    private fun getFilePath(): File {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "/" + resources.getString(R.string.app_name)
        )
        if (!file.exists()) {
            if (!file.mkdirs()) {
                CustomCookieToast(context).showFailureToast("Failed to create directory to save image.")
            }
        }
        return file
    }

    private class LoadDownload(
        val context: Activity,
        private val urls: String,
        private val directory: String,
        private val name: String,
        val layoutLoader: RelativeLayout,
        val onImageGenerated: () -> Unit
    ) : AsyncTask<String, String, String>() {

        private var bitmap2: Bitmap? = null

        @Deprecated("Deprecated in Java")
        @SuppressLint("WrongThread")
        override fun doInBackground(vararg strings: String): String {
            var inputStream: InputStream? = null
            try {
                inputStream = java.net.URL(urls).openStream()
                bitmap2 = BitmapFactory.decodeStream(inputStream)
            } catch (e: IOException) {
                e.printStackTrace()
                layoutLoader.visibility = View.GONE
            } finally {
                try {
                    inputStream?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                    layoutLoader.visibility = View.GONE
                }
            }
            return "0"
        }

        @Deprecated("Deprecated in Java")
        @SuppressLint("WrongThread")
        override fun onPostExecute(s: String) {
            super.onPostExecute(s)
            val file = File(directory)
            if (!file.exists()) {
                file.mkdirs()
            }
            try {
                val filePath = "$directory/$name.png"
                val fileOutputStream = FileOutputStream(filePath)
                bitmap2?.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
                fileOutputStream.close()
                MediaScannerConnection.scanFile(context, arrayOf(filePath), null) { _, uri ->
                    // Toast.makeText(context, "ExternalStorage Scanned $uri: FILE=$filePath", Toast.LENGTH_SHORT).show()
                }
                layoutLoader.visibility = View.GONE
                onImageGenerated()
                // CustomCookieToast(context).showSuccessToast("Successfully Download")
            } catch (e: Exception) {
                e.printStackTrace()
                CustomCookieToast(context).showFailureToast(e.message)
                layoutLoader.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayImageGeneratedPopup() {
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.image_generated_popup, null)
        builder.setView(customLayout)

        val txtTitle = customLayout.findViewById<TextView>(R.id.tvTitle)
        val txtDescription = customLayout.findViewById<TextView>(R.id.tvDescription)
        val txtClose = customLayout.findViewById<TextView>(R.id.txtClose)

        txtTitle.text = "Downloaded Successfully!"
        txtDescription.text = "Congratulations! Your AI generated image has\nbeen downloaded successfully."

        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.setCancelable(false)
        alertDialog.show()
        txtClose.setOnClickListener {
            alertDialog.dismiss()
            onBackPressedCallback.handleOnBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayDeleteImagePopup(context: Activity, artId: String) {
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.item_delete_image_popup, null)
        builder.setView(customLayout)

        val txtYesDelete = customLayout.findViewById<TextView>(R.id.txtYesDelete)
        val txtCancel = customLayout.findViewById<TextView>(R.id.txtCancel)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.setCancelable(true)
        alertDialog.show()
        txtYesDelete.setOnClickListener {
            alertDialog.dismiss()
            callDeleteMagicArtApi(artId)
        }
        txtCancel.setOnClickListener { alertDialog.dismiss() }
    }

    private fun callDeleteMagicArtApi(artId: String) {
        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
        mainViewModel.callDeleteMagicArtApi(context, artId, binding.layoutLoader.layoutLoader)
    }

    private fun setObserver() {
        mainViewModel.deleteMagicArt.observe(this) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE

                SessionAndCookies.saveBoolean(context, Constant.IS_MAGIC_ART_UPDATED, true)
                CustomCookieToast(context).showSuccessToast(data.message?:"")

                Handler(Looper.getMainLooper()).postDelayed({
                    onBackPressedCallback.handleOnBackPressed()
                },1200)
            }
        }
    }
}