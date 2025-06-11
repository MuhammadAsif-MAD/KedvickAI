package com.kedvik.ai.ui.dashboard.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kedvik.ai.databinding.ItemDashboardCategoriesBinding
import com.kedvik.ai.ui.dashboard.model.DashboardCategoriesModel

class DashboardCategoriesAdapter(activity: Activity, private var modelList: MutableList<DashboardCategoriesModel>, private val itemClickedInterface: ItemClickedInterface) : RecyclerView.Adapter<DashboardCategoriesAdapter.ViewHolder>() {

    interface ItemClickedInterface {
        fun onItemClicked(position: Int)
    }

    var context: Activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemDashboardCategoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = modelList[position]
        holder.binding.ivCategory.setImageResource(dataModel.image)
        holder.binding.tvTitle.text = dataModel.title
        holder.binding.tvDescription.text = dataModel.description

        holder.binding.rlParent.setOnClickListener {
            itemClickedInterface.onItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    class ViewHolder(val binding: ItemDashboardCategoriesBinding) : RecyclerView.ViewHolder(binding.root)
}
