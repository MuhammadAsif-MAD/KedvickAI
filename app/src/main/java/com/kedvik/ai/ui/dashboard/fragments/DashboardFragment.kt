package com.kedvik.ai.ui.dashboard.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvik.ai.ui.dashboard.adapters.DashboardCategoriesAdapter
import com.kedvik.ai.ui.dashboard.model.DashboardCategoriesModel
import com.kedvik.ai.R
import com.kedvik.ai.api.ApplicationClass
import com.kedvik.ai.databinding.FragmentDashboardBinding
import com.kedvik.ai.model.UserDataModel
import com.kedvik.ai.utils.AnalyticsUtil
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.CustomCookieToast
import com.kedvik.ai.utils.NetworkUtils
import com.kedvik.ai.utils.SessionAndCookies
import com.kedvik.ai.utils.Utilities
import com.kedvik.ai.viewmodel.MainViewModel
import com.kedvik.ai.viewmodel.MainViewModelFactory

class DashboardFragment : Fragment(), DashboardCategoriesAdapter.ItemClickedInterface {

    private val modelList = mutableListOf(
        DashboardCategoriesModel(
            R.drawable.research_center_ic,
            "Research Center",
            "Questions or special requests? Just ask AI for easy answers and personalized assistance."
        ),
        DashboardCategoriesModel(
            R.drawable.create_image_ic,
            "Create Image",
            "Describe, generate – AI transforms your vision into professional images effortlessly."
        ),
        DashboardCategoriesModel(
            R.drawable.text_to_speech_ic,
            "Text To Speech",
            "Turn text into beautiful voice with our professional-grade text-to-speech conversion."
        )
    )

    private var token = ""
    var name: String = ""
    var email: String = ""
    private var myId: String = ""
    private var imagePath: String = ""
    private var availableWords: String = ""
    private var availableImages: String = ""

    private lateinit var mainViewModel: MainViewModel
    private var itemClickedInterface: OnFragmentItemClickedInterface? = null
    private lateinit var binding: FragmentDashboardBinding
    private lateinit var context: Activity

    override fun onResume() {
        super.onResume()
        setViewsData()
       callUserProfileApi()
        callNotificationCountsApi()
        // Analytic event
       AnalyticsUtil.logEvent(context, "dashboard_screen", "Dashboard Screen")
   }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(layoutInflater)
        context = requireActivity()

        initViewModel()
        setClickListener()
        setAdapter()
        setObserver()

        return binding.root
    }

    private fun initViewModel() {
        // viewModel & repository initialize
        val repository = (context.application as ApplicationClass).apiRepository
        mainViewModel = ViewModelProvider(this, MainViewModelFactory(repository))[MainViewModel::class.java]
    }

    private fun callUserProfileApi() {
        mainViewModel.callUserProfileApi(context, myId, binding.layoutLoader.layoutLoader)
    }

    private fun callNotificationCountsApi() {
        if (NetworkUtils.isInternetAvailable(context)) {
            mainViewModel.callNotificationCountsApi(context, token, binding.layoutLoader.layoutLoader)
        }
    }

    private fun setObserver() {
        mainViewModel.userProfile.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { data ->
                saveProfileData(data)
            }
        }
        mainViewModel.notificationCounts.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let {dataModel->
                if (dataModel.status == true){
                    if (dataModel.data != null){
                        unreadCounts = dataModel.data.ticketUnreadCounter?:0
                        binding.imgSetting.badgeValue = unreadCounts ?:0
                    }else{
                        binding.imgSetting.badgeValue = 0
                    }
                }else{
                    binding.imgSetting.badgeValue = 0
                    CustomCookieToast(context).showFailureToast(dataModel.title?:"", dataModel.errors?.get(0)?.message ?:"")
                }
            }
        }
    }

    private fun saveProfileData(dataModel: UserDataModel) {
        SessionAndCookies.saveUserProfileData(context, dataModel)
        setViewsData()
    }

    private fun setViewsData() {
        myId = SessionAndCookies.getMyId(context) ?: ""
        token = SessionAndCookies.getUserToken(context)?:""
        imagePath = SessionAndCookies.getProfileImage(context) ?: ""
        name = SessionAndCookies.getUserName(context) ?: ""
        email = SessionAndCookies.getUserEmail(context) ?: ""
        availableWords = SessionAndCookies.getAvailableWord(context) ?: ""
        availableImages = SessionAndCookies.getAvailableImages(context) ?: ""

        if (!TextUtils.isEmpty(imagePath)) {
            Utilities.loadImage(context, imagePath, binding.ivUserImage)
        }else{
            binding.ivUserImage.setImageResource(R.drawable.place_holder)
        }
        binding.tvName.text = name
        binding.tvEmail.text = email
        binding.tvWordsCount.text = availableWords
        binding.tvImagesCounts.text = availableImages

    }

    private fun setClickListener() {
        binding.btnSetting.setOnClickListener {
            Constant.startSettingActivity(context)
        }
        binding.rlProfileImage.setOnClickListener {
            Constant.openImagePreviewActivity(context, binding.ivUserImage, imagePath)
        }
    }

    private fun setAdapter() {
        binding.recyclerView.layoutManager = GridLayoutManager(context, 2, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = DashboardCategoriesAdapter(context, modelList, this)
    }

    override fun onItemClicked(position: Int) {
        itemClickedInterface?.onItemClicked(position)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentItemClickedInterface) {
            itemClickedInterface = context
        } else {
            throw RuntimeException("$context must implement OnFragmentItemClickedInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        itemClickedInterface = null
    }

    interface OnFragmentItemClickedInterface {
        fun onItemClicked(position: Int)
    }
    companion object{
        var unreadCounts: Int? = null
    }

}