package com.kedvik.ai.ui.helpAndSupport.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kedvik.ai.R
import com.kedvik.ai.ui.helpAndSupport.models.FAQsDataModel

class FAQsChildAdapter(var context: Activity, modelList: List<FAQsDataModel>) :
    RecyclerView.Adapter<FAQsChildAdapter.ViewHolder>() {
    var modelList: List<FAQsDataModel>

    init {
        this.modelList = modelList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.single_faq_child_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!modelList[position].isChecked) {
            holder.imgForwardArrow.rotation = 90f
            holder.txtAnswer.visibility = View.GONE
        } else {
            holder.imgForwardArrow.rotation = 270f
            holder.txtAnswer.visibility = View.VISIBLE
        }
        holder.layoutParent.setOnClickListener {
            if (!modelList[position].isChecked) {
                holder.imgForwardArrow.rotation = 270f
                holder.txtAnswer.visibility = View.VISIBLE
                modelList[position].isChecked = true
            } else {
                holder.imgForwardArrow.rotation = 90f
                holder.txtAnswer.visibility = View.GONE
                modelList[position].isChecked = false
            }
        }
        holder.txtQuestion.text = modelList[position].question
        holder.txtAnswer.text = modelList[position].answer
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgForwardArrow: ImageView
        var txtQuestion: TextView
        var txtAnswer: TextView
        var layoutParent: RelativeLayout

        init {
            layoutParent = itemView.findViewById(R.id.layoutParent)
            txtQuestion = itemView.findViewById(R.id.txtQuestion)
            txtAnswer = itemView.findViewById(R.id.txtAnswer)
            imgForwardArrow = itemView.findViewById(R.id.imgForwardArrow)
        }
    }
}