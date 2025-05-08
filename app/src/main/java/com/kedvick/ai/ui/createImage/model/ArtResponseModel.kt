package com.kedvick.ai.ui.createImage.model

import com.google.gson.annotations.SerializedName

data class ArtResponseModel(

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("image")
    val image: String? = null,

    @field:SerializedName("query")
    val query: String? = null,

    @field:SerializedName("resolution")
    val resolution: String? = null
)