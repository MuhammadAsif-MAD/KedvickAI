package com.kedvick.ai.ui.helpAndSupport.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.databinding.ItemLoadingBinding
import com.kedvick.ai.databinding.ItemRcTxtMsgBinding
import com.kedvick.ai.databinding.ItemSeTxtMsgBinding
import com.kedvick.ai.ui.helpAndSupport.models.TicketDetailDataItemModel
import com.kedvick.ai.utils.Utilities

class TicketChatAdapter(var context: Activity, listItems: MutableList<TicketDetailDataItemModel>, myID: String) :
    RecyclerView.Adapter<TicketChatAdapter.ViewHolder>() {

    private var isLoadingAdded = false
    private var myID: String
    private var messagesList: MutableList<TicketDetailDataItemModel>
    var isLastPage = false

    init {
        messagesList = listItems
        this.myID = myID
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_LOADING -> {
                ViewHolder(ItemLoadingBinding.inflate(LayoutInflater.from(context), parent, false))
            }
            VIEW_TYPE_SENDER_MESSAGE -> {
                ViewHolder(ItemSeTxtMsgBinding.inflate(LayoutInflater.from(context), parent, false))
            }
            else -> {
                ViewHolder(ItemRcTxtMsgBinding.inflate(LayoutInflater.from(context), parent, false))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        if (getItemViewType(position) != VIEW_TYPE_LOADING){

            val fromID: String = java.lang.String.valueOf(messagesList[position].from)
            if (fromID == myID) {
                holder.senderTextBinding!!.rlMyTextMessage.visibility = View.VISIBLE
            } else {
                holder.receiverTextBinding!!.rlOtherTextMessage.visibility = View.VISIBLE
            }
            if (getItemViewType(position) == VIEW_TYPE_SENDER_MESSAGE){
                setSenderMessageValues(holder.senderTextBinding, position)
            }else{
                setReceiverMessageValues(holder.receiverTextBinding, position)
            }
        }
    }
    private fun setSenderMessageValues(binding: ItemSeTxtMsgBinding?, position: Int) {
        binding!!.tvMsg.text = messagesList[position].message
        binding.tvTime.visibility = View.VISIBLE
        binding.tvTime.text = Utilities.getPrettyTime(messagesList[position].time!!)
    }

    private fun setReceiverMessageValues(binding: ItemRcTxtMsgBinding?, position: Int) {
        binding!!.tvMsg.text = messagesList[position].message
        binding.tvTime.visibility = View.VISIBLE
        binding.tvTime.text = Utilities.getPrettyTime(messagesList[position].time!!)
        binding.ivAppLogo.visibility = View.VISIBLE
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == messagesList.size - 1 && isLoadingAdded) {
            VIEW_TYPE_LOADING
        } else if (messagesList[position].from == myID) {
            VIEW_TYPE_SENDER_MESSAGE
        } else {
            VIEW_TYPE_RECEIVER_MESSAGE
        }

    }

    override fun getItemCount(): Int {
        return messagesList.size
    }

    fun add(r: TicketDetailDataItemModel) {
        messagesList.add(r)
        notifyItemInserted(messagesList.size - 1)
    }

    fun addAll(moveResults: List<TicketDetailDataItemModel>) {
        for (result in moveResults) {
            add(result)
        }
    }

    val isEmpty: Boolean
        get() = itemCount == 0

    fun getItem(position: Int): TicketDetailDataItemModel {
        return messagesList[position]
    }

    fun add(i: Int, dataModel: TicketDetailDataItemModel) {
        messagesList.add(i, dataModel)
        notifyItemInserted(i)
    }

/*    class ViewHolder(itemView: View, viewType: Int) : RecyclerView.ViewHolder(itemView) {
        var txtTime: TextView
        var txtMsg: TextView
        var rlMyTextMessage: RelativeLayout
        var rlOtherTextMessage: RelativeLayout

        init {
            txtTime = itemView.findViewById(R.id.tvTime)
            txtMsg = itemView.findViewById(R.id.tvMsg)
            rlMyTextMessage = itemView.findViewById(R.id.rlMyTextMessage)
            rlOtherTextMessage = itemView.findViewById(R.id.rlOtherTextMessage)
        }
    }*/

    class ViewHolder : RecyclerView.ViewHolder {
        var loadingBinding: ItemLoadingBinding? = null
        var senderTextBinding: ItemSeTxtMsgBinding? = null
        var receiverTextBinding: ItemRcTxtMsgBinding? = null

        constructor(loadingBinding: ItemLoadingBinding) : super(loadingBinding.root) {
            this.loadingBinding = loadingBinding
        }

        constructor(senderTextBinding: ItemSeTxtMsgBinding) : super(senderTextBinding.root) {
            this.senderTextBinding = senderTextBinding
        }
        constructor(receiverTextBinding: ItemRcTxtMsgBinding) : super(receiverTextBinding.root) {
            this.receiverTextBinding = receiverTextBinding
        }
    }

    companion object {
        private const val VIEW_TYPE_RECEIVER_MESSAGE = 1
        private const val VIEW_TYPE_LOADING = 2
        private const val VIEW_TYPE_SENDER_MESSAGE = 3
    }
}