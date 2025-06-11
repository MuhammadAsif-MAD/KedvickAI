package com.kedvik.ai.ui.auth.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.kedvik.ai.R


class OnBoardingAdapter(private var context: Activity) : PagerAdapter() {

    private val imagesArray = arrayOf(
        R.drawable.img_onboarding_one,
        R.drawable.img_onboarding_two,
        R.drawable.img_onboarding_three
    )

    private val titleList = arrayOf(
        "Message Crafting Companion",
        "Endless Writing Solutions",
        "30 Languages, Infinite Writing"
    )

    private val descriptionList = arrayOf(
        "KEDVIK is your go-to tool for writing perfect\nmessages, whether it's emails, captions, or love\nletters.",
        "Discover over 1000 well-organized examples and\ntemplates to meet all your writing needs\neffortlessly.",
        "KEDVIK possesses the capability to understand and\ngenerate content in 30 languages, including English,\nSpanish, Japanese, and Portuguese."
    )

    private val mLayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return imagesArray.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == (`object`)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.item_onboarding, container, false)
        val imageView = itemView.findViewById<ImageView>(R.id.image)
        val tvTitle = itemView.findViewById<TextView>(R.id.tvTitle)
        val tvDescription = itemView.findViewById<TextView>(R.id.tvDescription)

        imageView.setImageResource(imagesArray[position])
        tvTitle.text = titleList[position]
        tvDescription.text = descriptionList[position]
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}