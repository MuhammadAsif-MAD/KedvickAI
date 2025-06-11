package com.kedvik.ai.ui.helpAndSupport.models

import com.google.gson.annotations.SerializedName

data class TicketCreateResponseModel(

    @field:SerializedName("data")
    val data: TicketCreateDataModel? = null,

    @field:SerializedName("action")
    val action: String? = null,

    @field:SerializedName("status")
    val status: Boolean? = null
)

data class TicketCreateDataModel(

    @field:SerializedName("id")
    val uuid: String? = null,

    @field:SerializedName("time")
    val dateTime: String? = null,

    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("created_at")
    val createdAt: String? = null,

    @field:SerializedName("type")
    val type: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("device")
    val device: String? = null,

    @field:SerializedName("app_version")
    val appVersion: String? = null,

    @field:SerializedName("category_id")
    val categoryId: String? = null,

    @field:SerializedName("status")
    val status: Int? = null
)
