package com.kedvick.ai.ui.textToSpeech.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kedvick.ai.ui.textToSpeech.model.TextToSpeechResponseModel
import com.kedvick.ai.databinding.ItemAvailableSpeechBinding

class ListOfAvailableSpeechAdapter(
    activity: Activity, private var modelList: MutableList<TextToSpeechResponseModel>,
    private val itemClickedInterface: ItemClickedInterface
) :
    RecyclerView.Adapter<ListOfAvailableSpeechAdapter.ViewHolder>() {

    interface ItemClickedInterface {
        fun onArrowButtonClicked(dataModel: TextToSpeechResponseModel)
//        fun onPlayButtonClicked(dataModel: TextToSpeechResponseModel)
//        fun onPauseButtonClicked(dataModel: TextToSpeechResponseModel)
//        fun onDownloadButtonClicked(dataModel: TextToSpeechResponseModel)
    }

    var context: Activity = activity

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAvailableSpeechBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataModel = modelList[position]

        holder.binding.tvTitle.text = dataModel.documentName
        holder.binding.tvDescription.text = dataModel.text


        holder.binding.llProfile.setOnClickListener {
            dataModel.adapterPosition = position
            itemClickedInterface.onArrowButtonClicked(dataModel)
        }

//        holder.binding.btnAudioPlay.setOnClickListener {
//            itemClickedInterface.onPlayButtonClicked(dataModel)
//        }
//        holder.binding.btnAudioPause.setOnClickListener {
//            itemClickedInterface.onPauseButtonClicked(dataModel)
//        }
//        holder.binding.btnAudioDownload.setOnClickListener {
//            itemClickedInterface.onDownloadButtonClicked(dataModel)
//        }
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

    class ViewHolder(val binding: ItemAvailableSpeechBinding) :
        RecyclerView.ViewHolder(binding.root)
}
