package com.kedvik.ai.ui.helpAndSupport.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kedvik.ai.R
import com.kedvik.ai.databinding.TicketItemBinding
import com.kedvik.ai.ui.helpAndSupport.activities.SupportChatActivity
import com.kedvik.ai.ui.helpAndSupport.models.TicketsListDataItemModel
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.Utilities

class TicketsAdapter(var context: Activity, var modelList: List<TicketsListDataItemModel>) :
    RecyclerView.Adapter<TicketsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            TicketItemBinding.inflate(
                LayoutInflater.from(
                    context
                )
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        try {
            val data: TicketsListDataItemModel = modelList[position]

            if (data.unread_count == "0"){
                holder.binding.rlCounts.visibility = View.GONE
            }else{
                holder.binding.rlCounts.visibility = View.VISIBLE
                holder.binding.tvHelpAndSupportCounts.text = data.unread_count
            }

            if (data.status == 0){
                holder.binding.ticketStatus.text = "Active"
            }else {
                holder.binding.ticketStatus.text = "Closed"
                holder.binding.ticketStatus.setTextColor(context.resources.getColor(R.color.red_color))
            }
            holder.binding.ticketNo.text = "#" + data.uuid
            holder.binding.ticketDateTime.text = Utilities.formatTimestamp(data.dateTime?:"")
            holder.binding.ticketTitle.text = data.type
            holder.binding.tvDescription.text = data.description

            holder.binding.llParent.setOnClickListener {
                holder.binding.rlCounts.visibility = View.GONE

                if (data.unread_count != "0"){
                    SupportChatActivity.isUnreadCountsUpdated = true
                }
                val intent = Intent(context, SupportChatActivity::class.java)
                intent.putExtra(Constant.TICKET_ID, data.uuid + "")
                intent.putExtra("ticket_position", position)
                intent.putExtra(Constant.TICKET_STATUS, data.status)
                intent.putExtra(Constant.CALL_FROM, Constant.COMPLAINT)
                context.startActivity(intent)
                context.overridePendingTransition(R.anim.right_to_left, R.anim.left_to_right)
            }
        } catch (e: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    class ViewHolder(itemBinding: TicketItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        var binding: TicketItemBinding

        init {
            binding = itemBinding
        }
    }
}