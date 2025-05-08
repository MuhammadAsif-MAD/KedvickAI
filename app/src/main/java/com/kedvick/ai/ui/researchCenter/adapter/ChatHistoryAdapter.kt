package com.kedvick.ai.ui.researchCenter.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.databinding.ItemChatHistoryBinding
import com.kedvick.ai.ui.researchCenter.model.ChatHistoryRecordsItemModel

class ChatHistoryAdapter(activity: Activity, private var modelList: MutableList<ChatHistoryRecordsItemModel>, private val itemClickedInterface: ItemClickedInterface) : RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder>() {

    interface ItemClickedInterface {
        fun onItemClicked(position: Int)
        fun onOptionButtonClick(v: View, position: Int)
    }

    var context: Activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChatHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val title = modelList[position]
        holder.binding.tvMessage.text = title.data[0].text

        holder.binding.btnChatHistory.setOnClickListener {
            itemClickedInterface.onItemClicked(position)
        }

        holder.binding.btnOptions.setOnClickListener { v->
            itemClickedInterface.onOptionButtonClick(v, position)
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    fun itemRemoved(position: Int) {
        modelList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, modelList.size)
    }

    class ViewHolder(val binding: ItemChatHistoryBinding) : RecyclerView.ViewHolder(binding.root)

}