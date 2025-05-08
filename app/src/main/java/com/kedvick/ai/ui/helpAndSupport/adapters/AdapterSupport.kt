package com.kedvick.ai.ui.helpAndSupport.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.R
import com.kedvick.ai.databinding.ItemSupportRequestBinding
import com.kedvick.ai.ui.helpAndSupport.models.ItemSupportReq

class AdapterSupport(var context: Context, var modelList: List<ItemSupportReq>, private val itemClickedInterface: ItemClickedInterface) :
    RecyclerView.Adapter<AdapterSupport.MyViewHolder?>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(ItemSupportRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (modelList.isNotEmpty()) {
            modelList.size
        } else 0
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.binding.tvTitle.text = modelList[position].subject
        holder.binding.tvCategory.text = modelList[position].category
        holder.binding.tvTicketID.text = modelList[position].ticketId
        holder.binding.tvDate.text = modelList[position].lastUpdated
        holder.binding.tvStatus.text = modelList[position].status
        val item: ItemSupportReq = modelList[position]
        if (item.status.equals("Open")) {
            holder.binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.blue_600)))
        } else if (item.status.equals("Replied")) {
            holder.binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.yellow_700)))
        } else if (item.status.equals("Pending")) {
            holder.binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.pink_700)))
        } else if (item.status.equals("Resolved")) {
            holder.binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.green_600)))
        } else if (item.status.equals("Closed")) {
            //Closed
            holder.binding.tvStatus.setBackgroundTintList(ColorStateList.valueOf(context.getColor(R.color.grey_80)))
            holder.itemView.setAlpha(0.4f)
        }
        if (item.priority.equals("High")) {
            holder.binding.ivPriority.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.red_700)))
        } else if (item.priority.equals("Normal")) {
            holder.binding.ivPriority.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.cyan_500)))
        } else if (item.priority.equals("Low")) {
            holder.binding.ivPriority.setImageTintList(ColorStateList.valueOf(context.getColor(R.color.green_600)))
        }

        holder.itemView.setOnClickListener{
            itemClickedInterface.onItemClicked(position, modelList[position])
        }

    }

    class MyViewHolder(val binding: ItemSupportRequestBinding) : RecyclerView.ViewHolder(binding.root)

    interface ItemClickedInterface {
        fun onItemClicked(position: Int, dataModel: ItemSupportReq)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSupportReq(supportReqList: List<ItemSupportReq>) {
        modelList = supportReqList
        notifyDataSetChanged()
    }
}
