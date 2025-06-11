package com.kedvik.ai.ui.dashboard.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvik.ai.ui.createImage.adapters.ArtListAdapter
import com.kedvik.ai.ui.createImage.model.ArtResponseModel
import com.kedvik.ai.R
import com.kedvik.ai.api.ApplicationClass
import com.kedvik.ai.databinding.FragmentImageBinding
import com.kedvik.ai.utils.AnalyticsUtil
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.viewmodel.MainViewModel
import com.kedvik.ai.viewmodel.MainViewModelFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ImageFragment : Fragment(), ArtListAdapter.ItemClickedInterface {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var rvAdapter: ArtListAdapter

    private var myId: String = ""
    private var selectedPosition: Int = 0

    var modelList: MutableList<ArtResponseModel> = arrayListOf()

    private lateinit var binding: FragmentImageBinding
    private lateinit var context: Activity

    override fun onResume() {
        super.onResume()
        if (SessionAndCookies.getBoolean(context, Constant.IS_MAGIC_ART_UPDATED)) {
            SessionAndCookies.saveBoolean(context, Constant.IS_MAGIC_ART_UPDATED, false)
            binding.llNoImageFound.visibility = View.GONE
            callMagicArtsApi(true)
        }

        // Analytic event
        AnalyticsUtil.logEvent(context, "create_image_screen", "Create Image Screen")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentImageBinding.inflate(layoutInflater)
        context = requireActivity()

        initData()
        setClickListener()
        initViewModel()
        callMagicArtsApi(true)
        setObserver()

        return binding.root
    }

    private fun setClickListener() {
        binding.rlCreateArt.setOnClickListener {
            Constant.startCreateArtActivity(context)
        }
        binding.rlCreateArtTwo.setOnClickListener {
            Constant.startCreateArtActivity(context)
        }
    }

    private fun initData() {
        myId = SessionAndCookies.getMyId(context) ?: ""
    }

    private fun initViewModel() {
        // viewModel & repository initialize
        val repository = (context.application as ApplicationClass).apiRepository
        mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]
    }

    private fun setAdapter() {
        binding.recyclerView.layoutManager =
            GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        rvAdapter = ArtListAdapter(context, modelList, this)
        binding.recyclerView.adapter = rvAdapter
    }

    override fun onItemClicked(position: Int, dataModel: ArtResponseModel) {
        selectedPosition = position

        Constant.openArtPreviewActivity(context, dataModel.image ?: "", dataModel.id ?: "")
//         displayDeleteOrDownloadImagePopup(dataModel)
    }

    @SuppressLint("SetTextI18n")
//    private fun displayDeleteOrDownloadImagePopup(dataModel: ArtResponseModel) {
//        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
//        val customLayout: View = LayoutInflater.from(context).inflate(R.layout.picture_delete_download_popup, null)
//        builder.setView(customLayout)
//
//        val tvPrompt = customLayout.findViewById<TextView>(R.id.tvPrompt)
//        val tvSize = customLayout.findViewById<TextView>(R.id.tvSize)
//        val tvDelete = customLayout.findViewById<TextView>(R.id.tvDelete)
//        val tvDownload = customLayout.findViewById<TextView>(R.id.tvDownload)
//        val imageView = customLayout.findViewById<RoundedImageView>(R.id.image)
//
//        tvPrompt.text = dataModel.query
//        tvSize.text = dataModel.resolution
//        Utilities.loadImage(context, dataModel.image, imageView)
//
//        val alertDialog: AlertDialog = builder.create()
//        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
//        alertDialog.setCancelable(true)
//        alertDialog.show()
//        tvDelete.setOnClickListener {
//            alertDialog.dismiss()
//            displayDeleteImagePopup(context, dataModel.id.toString())
//        }
//        tvDownload.setOnClickListener {
//            alertDialog.dismiss()
//            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
//            val name = "MagicArt_" + System.currentTimeMillis()
//            val loadLogo = LoadDownload(
//                context,
//                dataModel.image.toString(),
//                getFilePath().absolutePath,
//                name,
//                binding.layoutLoader.layoutLoader
//            )
//            loadLogo.execute()
//        }
//    }

//    @SuppressLint("SetTextI18n")
//    private fun displayDeleteImagePopup(context: Activity, artId: String) {
//        val builder = AlertDialog.Builder(context, R.style.CustomDialog)
//        val customLayout: View =
//            LayoutInflater.from(context).inflate(R.layout.item_delete_image_popup, null)
//        builder.setView(customLayout)
//
//        val txtYesDelete = customLayout.findViewById<TextView>(R.id.txtYesDelete)
//        val txtCancel = customLayout.findViewById<TextView>(R.id.txtCancel)
//
//        val alertDialog: AlertDialog = builder.create()
//        alertDialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
//        alertDialog.setCancelable(true)
//        alertDialog.show()
//        txtYesDelete.setOnClickListener {
//            alertDialog.dismiss()
//            callDeleteMagicArtApi(artId)
//        }
//        txtCancel.setOnClickListener { alertDialog.dismiss() }
//    }

//    private fun callDeleteMagicArtApi(artId: String) {
//        binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
//        mainViewModel.callDeleteMagicArtApi(context, artId, binding.layoutLoader.layoutLoader)
//    }

    private fun callMagicArtsApi(isLoaderShow: Boolean) {
        if (isLoaderShow) {
            binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            if (modelList.size == 0) {
                binding.topLayoutLoader.layoutLoader.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            }
        }
        mainViewModel.callGetMagicArtApi(context, myId, binding.topLayoutLoader.layoutLoader)
    }

    private fun setObserver() {
        mainViewModel.getMagicArt.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.topLayoutLoader.layoutLoader.visibility = View.GONE
                modelList = data

                if (modelList.size == 0) {
                    binding.llNoImageFound.visibility = View.VISIBLE
                    binding.rlCreateArtTwo.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                } else {
                    binding.llNoImageFound.visibility = View.GONE
                    binding.rlCreateArtTwo.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.VISIBLE
                }
                setAdapter()
            }
        }
        mainViewModel.deleteMagicArt.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { data ->
                binding.layoutLoader.layoutLoader.visibility = View.GONE

                SessionAndCookies.saveBoolean(context, Constant.IS_MAGIC_ART_UPDATED, true)
                CustomCookieToast(context).showSuccessToast(data.message ?: "")
                rvAdapter.itemRemoved(selectedPosition)
            }
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
        private val context: Activity,
        private val urls: String,
        private val directory: String,
        private val name: String,
        val layoutLoader: RelativeLayout
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
                MediaScannerConnection.scanFile(context, arrayOf(filePath), null) { _, _ ->
                    // Toast.makeText(context, "ExternalStorage Scanned $uri: FILE=$filePath", Toast.LENGTH_SHORT).show()
                }
                layoutLoader.visibility = View.GONE
                CustomCookieToast(context).showSuccessToast("Successfully Download")
            } catch (e: Exception) {
                e.printStackTrace()
                CustomCookieToast(context).showFailureToast(e.message)
                layoutLoader.visibility = View.GONE
            }
        }
    }

}