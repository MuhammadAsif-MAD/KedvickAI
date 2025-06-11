package com.kedvik.ai.ui.helpAndSupport.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kedvik.ai.databinding.ItemRcTxtMsgBinding
import com.kedvik.ai.databinding.ItemSeTxtMsgBinding
import com.kedvik.ai.ui.helpAndSupport.models.Message
import com.kedvik.ai.utils.Utilities

class AdapterChat(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val CHAT_ME = 100
    private val CHAT_ADMIN = 200
    private var messageList: MutableList<Message> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == CHAT_ME) {
            val binding = ItemSeTxtMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MeViewHolder(binding)
        } else {
            val binding2: ItemRcTxtMsgBinding = ItemRcTxtMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AdminViewHolder(binding2)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MeViewHolder) {
            holder.binding.tvMsg.text = messageList[position].message
            holder.binding.tvTime.visibility = View.VISIBLE
            holder.binding.tvTime.text = messageList[position].date
            if (messageList[position].image != "") {
                Utilities.loadImage(context, messageList[position].image, holder.binding.imgMessage)
                holder.binding.imgMessage.setVisibility(View.VISIBLE)

            }
        } else {
            val viewHolder = holder as AdminViewHolder
            viewHolder.binding2.tvMsg.text = messageList[position].message
            viewHolder.binding2.tvTime.visibility = View.VISIBLE
            viewHolder.binding2.tvTime.text = messageList[position].date
            if (messageList[position].image != "") {
                Utilities.loadImage(context, messageList[position].image, holder.binding2.imgMessage)
                holder.binding2.imgMessage.setVisibility(View.VISIBLE)
            }
        }
    }

    override fun getItemCount(): Int {
        return if (messageList != null && messageList!!.isNotEmpty()) {
            messageList.size
        } else 0
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMessageList(modelList: MutableList<Message>) {
        this.messageList = modelList
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messageList != null) {
            if (messageList[position].type != "assistant") {
                CHAT_ME
            } else {
                CHAT_ADMIN
            }
        } else super.getItemViewType(position)
    }

    inner class MeViewHolder(var binding: ItemSeTxtMsgBinding) : RecyclerView.ViewHolder(
        binding.getRoot()
    )

    inner class AdminViewHolder(var binding2: ItemRcTxtMsgBinding) : RecyclerView.ViewHolder(
        binding2.getRoot()
    )

}
