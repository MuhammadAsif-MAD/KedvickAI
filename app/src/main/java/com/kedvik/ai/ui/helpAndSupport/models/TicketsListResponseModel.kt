package com.kedvik.ai.ui.helpAndSupport.models

import com.google.gson.annotations.SerializedName

data class TicketsListResponseModel(

	@field:SerializedName("data")
	val data: List<TicketsListDataItemModel>,

	@field:SerializedName("action")
	val action: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class TicketsListDataItemModel(

	@field:SerializedName("time")
	val dateTime: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: String? = null,

	@field:SerializedName("message")
	val description: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("category")
	val type: String? = null,

	@field:SerializedName("id")
	val uuid: String? = null,

	@field:SerializedName("status")
	val status: Int? = null,

	@field:SerializedName("unread_count")
	val unread_count: String? = null
)