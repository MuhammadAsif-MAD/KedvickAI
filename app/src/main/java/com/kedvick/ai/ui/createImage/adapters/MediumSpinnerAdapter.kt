package com.kedvick.ai.ui.createImage.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kedvick.ai.R

class MediumSpinnerAdapter(
    private var context: Context,
    private var flags: IntArray,
    private var countryNames: Array<String>
) : BaseAdapter() {

    private var inflter: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return flags.size
    }

    override fun getItem(i: Int): Any {
        // Return a pair of flag and country name or just the country name if you prefer
        return countryNames[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("ViewHolder", "InflateParams")
    override fun getView(i: Int, convertView: View?, viewGroup: ViewGroup): View {
        val view = inflter.inflate(R.layout.item_custom_spinner, null)
        val icon = view.findViewById<ImageView>(R.id.imageView)
        val names = view.findViewById<TextView>(R.id.textView)
        icon.setImageResource(flags[i])
        names.text = countryNames[i]
        /*if (i == 0){
            icon.visibility = View.GONE
        }else{
            icon.visibility = View.VISIBLE
        }*/
        return view
    }

    override fun getDropDownView(i: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflter.inflate(R.layout.item_custom_spinner, parent, false)
        val icon = view.findViewById<ImageView>(R.id.imageView)
        val names = view.findViewById<TextView>(R.id.textView)
        icon.setImageResource(flags[i])
        names.text = countryNames[i]
        return view
    }
}
