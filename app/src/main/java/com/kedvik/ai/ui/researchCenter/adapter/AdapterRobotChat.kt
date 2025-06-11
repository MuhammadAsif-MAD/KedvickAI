package com.kedvik.ai.ui.researchCenter.adapter

import android.app.Activity
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kedvik.ai.R
import com.kedvik.ai.api.RetrofitBuilder
import com.kedvik.ai.databinding.ItemRcTxtMsgBinding
import com.kedvik.ai.databinding.ItemSeTxtMsgBinding
import com.kedvik.ai.ui.researchCenter.model.ChatDataItemModel
import com.kedvik.ai.utils.Constant
import com.kedvik.ai.utils.Utilities

class AdapterRobotChat(
    var context: Activity,
    private var modelList: MutableList<ChatDataItemModel>,
    var callFor: String,
    private val itemClickListener: ItemClickedInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val CHATME = 100
    private val CHAT_ADMIN = 200
    private var SELECTED_VIEW: View? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == CHATME) {
            val binding = ItemSeTxtMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MeViewHolder(binding)
        } else {
            val binding2 = ItemRcTxtMsgBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            AdminViewHolder(binding2)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MeViewHolder) {
            val binding = holder.binding
            val dataModel = modelList[position]

            if (!TextUtils.isEmpty(dataModel.text) && !TextUtils.isEmpty(dataModel.media)){
                binding.tvMsg.visibility = View.VISIBLE
                binding.rlAttachedMedia.visibility = View.VISIBLE
                binding.rlAttachedPdf.visibility = View.GONE
                binding.imgMessage.visibility = View.VISIBLE

                binding.tvMsg.text = Html.fromHtml(modelList[position].text)
                if (!TextUtils.isEmpty(dataModel.media)) {
                    Utilities.loadChatImage(context, dataModel.media, binding.imgMessage)
                }else{
                    binding.imgMessage.setImageResource(R.drawable.place_holder)
                }
            }
            else if (!TextUtils.isEmpty(dataModel.text) && !TextUtils.isEmpty(dataModel.pdf)){
                binding.tvMsg.visibility = View.VISIBLE
                binding.rlAttachedMedia.visibility = View.VISIBLE
                binding.rlAttachedPdf.visibility = View.VISIBLE
                binding.imgMessage.visibility = View.GONE

                binding.tvMsg.text = Html.fromHtml(modelList[position].text)
            }
            else if (!TextUtils.isEmpty(dataModel.media)){
                binding.tvMsg.visibility = View.GONE
                binding.rlAttachedMedia.visibility = View.VISIBLE
                binding.rlAttachedPdf.visibility = View.GONE
                binding.imgMessage.visibility = View.VISIBLE

                binding.tvMsg.visibility = View.GONE
                if (!TextUtils.isEmpty(dataModel.media)) {
                    Utilities.loadImage(context, dataModel.media, binding.imgMessage)
                }else{
                    binding.imgMessage.setImageResource(R.drawable.place_holder)
                }
            }
            else if (!TextUtils.isEmpty(dataModel.pdf)){
                binding.tvMsg.visibility = View.GONE
                binding.rlAttachedMedia.visibility = View.VISIBLE
                binding.rlAttachedPdf.visibility = View.VISIBLE
                binding.imgMessage.visibility = View.GONE
            }
            else{
                binding.tvMsg.visibility = View.VISIBLE
                binding.rlAttachedMedia.visibility = View.GONE
                binding.rlAttachedPdf.visibility = View.GONE
                binding.imgMessage.visibility = View.GONE

                binding.tvMsg.visibility = View.VISIBLE
                binding.tvMsg.text = Html.fromHtml(modelList[position].text)
            }

            binding.imgMessage.setOnClickListener {
                if (dataModel.media?.contains("chat/media") == true){
                    val media = RetrofitBuilder.MEDIA_URL + dataModel.media
                    Constant.openImagePreviewActivity(context, binding.imgMessage, media)
                }else{
                    Constant.openImagePreviewActivity(context, binding.imgMessage, dataModel.media?:"")
                }
            }
            binding.rlAttachedPdf.setOnClickListener {
                var pdfUrl = RetrofitBuilder.MEDIA_URL + dataModel.pdf
                pdfUrl = "https://docs.google.com/gview?embedded=true&url=$pdfUrl"
                Constant.startWebViewActivity(context, "Document", pdfUrl)
            }
        } else {
            val viewHolder = holder as AdminViewHolder
            viewHolder.binding2.successAnimation.setVisibility(View.VISIBLE)
            viewHolder.binding2.tvMsg.visibility = View.GONE

            when (modelList[position].text) {
                "LOADING" -> {
                    viewHolder.binding2.successAnimation.setVisibility(View.VISIBLE)
                    viewHolder.binding2.tvMsg.visibility = View.GONE
                }
                "" -> {
                    viewHolder.binding2.successAnimation.setVisibility(View.GONE)
                    viewHolder.binding2.tvMsg.visibility = View.GONE
                }
                else -> {
                    viewHolder.binding2.successAnimation.setVisibility(View.GONE)
                    viewHolder.binding2.tvMsg.visibility = View.VISIBLE
                    viewHolder.binding2.tvMsg.text = Html.fromHtml(modelList[position].text)
                }
            }
        }

        holder.itemView.setOnLongClickListener { v ->
            SELECTED_VIEW = v
            val selectedText = modelList[position].text?:""
            itemClickListener.onItemClicked(position, modelList[position])
            false
        }
    }

    override fun getItemCount(): Int {
        return if (modelList.isNotEmpty()) {
            modelList.size
        } else 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (modelList[position].role != "assistant") {
            CHATME
        } else {
            CHAT_ADMIN
        }
    }

    inner class MeViewHolder(var binding: ItemSeTxtMsgBinding) : RecyclerView.ViewHolder(
        binding.getRoot()
    )

    inner class AdminViewHolder(var binding2: ItemRcTxtMsgBinding) : RecyclerView.ViewHolder(
        binding2.getRoot()
    )

    fun addMessages(modelList: MutableList<ChatDataItemModel>) {
        this.modelList = modelList
        notifyItemInserted(modelList.size)
    }

    interface ItemClickedInterface {
        fun onItemClicked(position: Int, dataModel: ChatDataItemModel)
    }

    fun getSelectedItemView(): View { return SELECTED_VIEW!! }
}
