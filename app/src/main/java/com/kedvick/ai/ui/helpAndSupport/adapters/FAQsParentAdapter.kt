package com.askrit.app.ui.helpAndSupport.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.R
import com.kedvick.ai.ui.helpAndSupport.adapters.FAQsChildAdapter
import com.kedvick.ai.ui.helpAndSupport.models.FAQsDataModel

class FAQsParentAdapter(var context: Activity, modelList: List<FAQsDataModel>) :
    RecyclerView.Adapter<FAQsParentAdapter.ViewHolder>() {
    private var childAdapter: FAQsChildAdapter? = null
    private var modelList: List<FAQsDataModel>
    private var questionList: List<FAQsDataModel>

    init {
        this.modelList = modelList
        questionList = ArrayList<FAQsDataModel>()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(context).inflate(R.layout.single_faq_parent_layout, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // questionList = modelList[position].faqs
        val model: FAQsDataModel = modelList[position]
        if (!modelList[position].isChecked) {
            holder.imgForwardArrow.rotation = 90f
            holder.recyclerView.visibility = View.GONE
        } else {
            holder.imgForwardArrow.rotation = 270f
            holder.recyclerView.visibility = View.VISIBLE
        }
        holder.itemView.setOnClickListener {
            if (!modelList[position].isChecked) {
                holder.imgForwardArrow.rotation = 270f
                holder.recyclerView.visibility = View.VISIBLE
                model.isChecked = true
            } else {
                holder.imgForwardArrow.rotation = 90f
                holder.recyclerView.visibility = View.GONE
                model.isChecked = false
            }
        }
        // holder.txtType.text = modelList[position].category
        holder.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        childAdapter = FAQsChildAdapter(context, questionList)
        holder.recyclerView.adapter = childAdapter
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgForwardArrow: ImageView
        var txtType: TextView
        var recyclerView: RecyclerView

        init {
            recyclerView = itemView.findViewById(R.id.childRecycler)
            txtType = itemView.findViewById(R.id.txtType)
            imgForwardArrow = itemView.findViewById(R.id.imgForwardArrow)
        }
    }
}