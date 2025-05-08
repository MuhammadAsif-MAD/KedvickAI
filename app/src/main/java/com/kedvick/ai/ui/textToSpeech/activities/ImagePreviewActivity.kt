package com.kedvick.ai.ui.textToSpeech.activities

import android.app.Activity
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.MediaStoreSignature
import com.jsibbold.zoomage.ZoomageView
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivityImagePreviewBinding
import com.kedvick.ai.utils.Utilities
import jp.wasabeef.glide.transformations.BlurTransformation
import java.util.Calendar

class ImagePreviewActivity : AppCompatActivity() {

    private lateinit var blurImage: ImageView
    private lateinit var imageDetailed: ZoomageView
    lateinit var image: String

    private lateinit var onBackPressedCallback: OnBackPressedCallback
    private lateinit var binding: ActivityImagePreviewBinding
    lateinit var context: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@ImagePreviewActivity

        Utilities.hideNavAndStatusBar(context)

        supportPostponeEnterTransition()

        val extras = intent.extras
        val imageTransitionName = extras!!.getString("animationName")


        image = intent.getStringExtra("image")!!

        imageDetailed = findViewById(R.id.image_Detailed)
        blurImage = findViewById(R.id.blurImage)

        imageDetailed.transitionName = imageTransitionName
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

        binding.btnCross.setOnClickListener { onBackPressedCallback.handleOnBackPressed()}

        supportStartPostponedEnterTransition()

        onBackPressCallback()
    }

    private fun onBackPressCallback() {
        onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.anim_stay, R.anim.exit_to_bottom)
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

}