package com.kedvik.ai.ui.createImage.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.kedvik.ai.ui.createImage.adapters.MediumSpinnerAdapter
import com.kedvik.ai.ui.createImage.adapters.StyleSpinnerAdapter
import com.jaredrummler.materialspinner.MaterialSpinner
import com.kedvik.ai.R
import com.kedvik.ai.databinding.ActivityCreateArtBinding
import com.kedvik.ai.ui.auth.activities.BaseActivity
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.utils.Utilities
import kotlin.math.abs

class CreateArtActivity : BaseActivity(){

    private var styleMenuNamesList = arrayOf("None", "3D Render", "Abstract", "Anime", "Cartoon", "Digital Art", "illustration", "Origami", "Pixel Art", "Photography", "Pop Art", "Retro", "Sketch", "Vaporware")
    private var styleMenuImagesList = intArrayOf(R.drawable.md_acrylic, R.drawable.st_3d, R.drawable.st_abstract, R.drawable.st_anime, R.drawable.st_cartoon, R.drawable.st_digital_art, R.drawable.st_illustration, R.drawable.st_origami, R.drawable.st_pixel_art, R.drawable.st_photography, R.drawable.st_pop_art, R.drawable.st_retro, R.drawable.st_sketch, R.drawable.st_vaporware)

    private var mediumMenuNamesList = arrayOf("None", "Acrylic", "Canvas", "Chalk", "Charcoal", "Crayon", "Glass", "Ink", "Pastel", "Pencil", "Spray Paint", "Water Color")
    private var mediumMenuImagesList = intArrayOf(R.drawable.md_acrylic, R.drawable.md_acrylic, R.drawable.md_canvas, R.drawable.md_chalk, R.drawable.md_charcoal, R.drawable.md_crayon, R.drawable.md_glass, R.drawable.md_ink, R.drawable.md_pastel, R.drawable.md_pencil, R.drawable.md_spray_paint, R.drawable.md_watercolor)

    private val noOfImagesList = mutableListOf("1", "2", "3", "4", "5")
    private val resolutionList = mutableListOf("[256x256] Small Image", "[512x512] Medium Image", "[1024x1024] Large Image")

    lateinit var myId: String
    private lateinit var availableImages: String
    private lateinit var instruction: String
    private lateinit var selectedStyle: String
    private lateinit var selectedMedium: String
    private var resolution: String = "[256x256] Small Image"
    private var noOfImages: String = "1"

    private lateinit var binding: ActivityCreateArtBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateArtBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@CreateArtActivity

        initSpinner()
        initData()
        setTextWatcher()
        setClickListener()
        setObserver()
    }

    private fun initSpinner() {
        // Style Spinner
        val styleSpinner = findViewById<View>(R.id.mSpinnerStyle) as Spinner
        val styleSpinnerAdapter = StyleSpinnerAdapter(applicationContext, styleMenuImagesList, styleMenuNamesList)
        styleSpinner.setAdapter(styleSpinnerAdapter)

        styleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedStyle = if (position == 0) {
                    ""
                } else {
                    parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        // Medium Spinner
        val mediumSpinner = findViewById<View>(R.id.mSpinnerMedium) as Spinner
        val mediumSpinnerAdapter = MediumSpinnerAdapter(applicationContext, mediumMenuImagesList, mediumMenuNamesList)
        mediumSpinner.setAdapter(mediumSpinnerAdapter)

        mediumSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedMedium = if (position == 0) {
                    ""
                } else {
                    parent.getItemAtPosition(position).toString()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }

        binding.mSpinnerResolution.setItems(resolutionList)
        binding.mSpinnerNoOfImages.setItems(noOfImagesList)

        binding.mSpinnerResolution.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener { _: MaterialSpinner?, _: Int, _: Long, item: String ->
            resolution = item
        } as MaterialSpinner.OnItemSelectedListener<String>)

        binding.mSpinnerNoOfImages.setOnItemSelectedListener(MaterialSpinner.OnItemSelectedListener { _: MaterialSpinner?, _: Int, _: Long, item: String ->
            noOfImages = item
        } as MaterialSpinner.OnItemSelectedListener<String>)
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
        myId = SessionAndCookies.getMyId(context)?:""
        availableImages = SessionAndCookies.getAvailableImages(context)?:""
        binding.mSpinnerResolution.text = "[256x256] Small Image"
        binding.mSpinnerNoOfImages.text = "1"
        binding.mSpinnerResolution.setTextColor(Color.WHITE)
        binding.mSpinnerNoOfImages.setTextColor(Color.WHITE)

        if (availableImages == "1"){
            binding.tvAvailableImages.text = "$availableImages Image"
        }else{
            binding.tvAvailableImages.text = "$availableImages Images"
        }
    }

    private fun setTextWatcher() {
        binding.edInstruction.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            @SuppressLint("SetTextI18n")
            override fun afterTextChanged(s: Editable) {
                val maxChar = s.toString().length.toString()
                binding.tvMaxChar.text = "$maxChar/300"
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun setClickListener() {
        binding.btnBack.setOnClickListener {
            onBackPressedCallback.handleOnBackPressed()
        }

        binding.btnReset.setOnClickListener {
            binding.edInstruction.setText("")
            binding.tvMaxChar.text = "0/300"
            binding.mSpinnerStyle.setSelection(0)
            binding.mSpinnerMedium.setSelection(0)
            binding.mSpinnerResolution.text = "[256x256] Small Image"
            binding.mSpinnerNoOfImages.text = "1"
            binding.mSpinnerResolution.setTextColor(Color.WHITE)
            binding.mSpinnerNoOfImages.setTextColor(Color.WHITE)
        }

        binding.btnGenerate.setOnClickListener {
            startGenerating()
        }

    }

    private fun startGenerating() {
        SessionAndCookies.saveBoolean(context, Constant.IS_SERVICE_USED, true)
        instruction = binding.edInstruction.text.toString().trim()

        resolution = when (resolution) {
            "[256x256] Small Image" -> {
                "256x256"
            }
            "[512x512] Medium Image" -> {
                "512x512"
            }
            else -> {
                "1024x1024"
            }
        }

        if (TextUtils.isEmpty(instruction)){
            CustomCookieToast(context).showRequiredToast("Please write instruction")
        }else if (Integer.parseInt(noOfImages) > Integer.parseInt(availableImages)){
             CustomCookieToast(context).showFailureToast("You have insufficient image credit")
        }else{
            callCreateMagicArtApi()
        }
    }

    private fun callCreateMagicArtApi() {
        // Api Call
        Utilities.hideKeyboard(context)
        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
        mainViewModel.callCreateMagicArtApi(context, myId, instruction, selectedStyle, selectedMedium, noOfImages, resolution, binding.layoutLoader.layoutLoader)
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        mainViewModel.createMagicArt.observe(this)  { event ->
            event.getContentIfNotHandled()?.let { dataModel ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE
                if (dataModel.size > 0){
                    val computedValue = Integer.parseInt(availableImages) - Integer.parseInt(noOfImages)
                    availableImages = abs(computedValue).toString()

                    if (availableImages == "1"){
                        binding.tvAvailableImages.text = "$availableImages Image"
                    }else{
                        binding.tvAvailableImages.text = "$availableImages Images"
                    }

                    SessionAndCookies.saveAvailableImages(context, availableImages)
                    SessionAndCookies.saveBoolean(context, Constant.IS_MAGIC_ART_UPDATED, true)
                    displayImageGeneratedPopup()
                }
            }
        }
    }

    private fun displayImageGeneratedPopup() {
        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.image_generated_popup, null)
        builder.setView(customLayout)

        val txtClose = customLayout.findViewById<TextView>(R.id.txtClose)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
        alertDialog.setCancelable(false)
        alertDialog.show()
        txtClose.setOnClickListener {
            alertDialog.dismiss()

            onBackPressedCallback.handleOnBackPressed()
        }
    }

}