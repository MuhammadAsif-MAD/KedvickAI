package com.kedvik.ai.ui.textToSpeech.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TextToSpeechResponseModel(

    @field:SerializedName("documentId")
    val documentId: Int? = null,

    @field:SerializedName("documentName")
    val documentName: String? = null,

    var adapterPosition: Int = -1,

    @field:SerializedName("text")
    val text: String? = null,

    @field:SerializedName("audio_path")
    val audioPath: String? = null,

    @field:SerializedName("content_word")
    val contentWord: String? = null

) : Parcelable