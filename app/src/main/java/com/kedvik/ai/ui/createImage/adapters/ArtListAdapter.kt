package com.kedvik.ai.ui.createImage.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kedvik.ai.databinding.ItemPictureGeneratedBinding
import com.kedvik.ai.ui.createImage.model.ArtResponseModel
import com.kedvik.ai.utils.DoubleClickListener
import com.kedvik.ai.utils.Utilities

class ArtListAdapter(activity: Activity, private var modelList: MutableList<ArtResponseModel>, private val itemClickedInterface: ItemClickedInterface) : RecyclerView.Adapter<ArtListAdapter.ViewHolder>() {

    interface ItemClickedInterface {
        fun onItemClicked(position: Int, dataModel: ArtResponseModel)
    }

    var context: Activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPictureGeneratedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val dataModel = modelList[position]

        val templateImage = dataModel.image
        Utilities.loadImageWithoutAvatar(context, templateImage, holder.binding.image)

        holder.itemView.setOnClickListener(object : DoubleClickListener() {
            override fun onSingleClick(v: View?) {
                itemClickedInterface.onItemClicked(position, dataModel)
            }
        })
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    fun itemRemoved(position: Int) {
        modelList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, modelList.size)
    }

    class ViewHolder(val binding: ItemPictureGeneratedBinding) : RecyclerView.ViewHolder(binding.root)
}
