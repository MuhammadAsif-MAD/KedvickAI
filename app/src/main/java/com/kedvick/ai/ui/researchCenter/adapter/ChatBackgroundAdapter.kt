package com.kedvick.ai.ui.researchCenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.databinding.ItemBackgroundBinding
import com.kedvick.ai.ui.researchCenter.model.ChatBackgroundsModel

class ChatBackgroundAdapter(var context: Context, private var modelList: List<ChatBackgroundsModel>, private var clickListener: ItemClickedInterface) : RecyclerView.Adapter<ChatBackgroundAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemBackgroundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.ivBg.setImageDrawable(modelList[position].drawable)

        if (modelList[position].isSelected){
            holder.binding.ivSelectedTick.visibility = View.VISIBLE
        }else{
            holder.binding.ivSelectedTick.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { clickListener.onBackgroundItemClicked(position) }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    class MyViewHolder(var binding: ItemBackgroundBinding) : RecyclerView.ViewHolder(
        binding.getRoot()
    )

    interface ItemClickedInterface {
        fun onBackgroundItemClicked(position: Int)
    }
}