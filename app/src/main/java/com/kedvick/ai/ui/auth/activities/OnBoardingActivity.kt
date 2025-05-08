package com.kedvick.ai.ui.auth.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ActivityOnBoardingBinding
import com.kedvick.ai.ui.auth.adapters.OnBoardingAdapter
import com.kedvick.ai.utils.Constant
import com.zhpan.indicator.enums.IndicatorSlideMode
import com.zhpan.indicator.enums.IndicatorStyle

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnBoardingBinding
    lateinit var context: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        context = this@OnBoardingActivity

        initViewPager()

        binding.rlSkipButton.setOnClickListener {
            Constant.startSocialLoginScreen(context)
        }
    }

    private fun initViewPager() {
        binding.btnNext.setOnClickListener {
            if (binding.viewPager.currentItem == 2) {
                Constant.startSocialLoginScreen(context)
            } else {
                val nextPage = binding.viewPager.currentItem + 1
                binding.viewPager.currentItem = nextPage
            }
        }

        setImagesAdapterToSlider()
        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    binding.btnNext.text = "Done"
                    binding.rlSkipButton.visibility = View.INVISIBLE
                    binding.rlSkipButton.isEnabled = false
                } else {
                    binding.btnNext.text = "Next"
                    binding.rlSkipButton.visibility = View.VISIBLE
                    binding.rlSkipButton.isEnabled = true
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })

        onBackPressCallback()
    }

    private fun setImagesAdapterToSlider() {
        val pagerAdapter = OnBoardingAdapter(this)
        binding.viewPager.adapter = pagerAdapter
        setupIndicatorView()
    }

    private fun setupIndicatorView() {
        binding.pageIndicator.setSliderColor(ContextCompat.getColor(context,R.color.white), ContextCompat.getColor(context, R.color.white))
        binding.pageIndicator.setSliderWidth(getResources().getDimension(R.dimen.text_5), getResources().getDimension(R.dimen.text_24))
        binding.pageIndicator.setSliderHeight(getResources().getDimension(R.dimen.text_5))
        binding.pageIndicator.setSlideMode(IndicatorSlideMode.SCALE)
        binding.pageIndicator.setIndicatorStyle(IndicatorStyle.ROUND_RECT)
        binding.pageIndicator.setPageSize(3)
        binding.pageIndicator.setupWithViewPager(binding.viewPager)
    }

    private fun onBackPressCallback() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.activity_enter, R.anim.activity_exit)
            }
        })
    }

}