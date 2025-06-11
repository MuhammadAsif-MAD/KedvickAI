package com.kedvik.ai.ui.helpAndSupport.models

import com.google.gson.annotations.SerializedName

class Message(
    @field:SerializedName("type")
    var type: String? = null,

    @field:SerializedName("message")
    var message: String? = null,

    @field:SerializedName("date")
    var date: String? = null,

    @field:SerializedName("image")
    var image: String? = null,
)
