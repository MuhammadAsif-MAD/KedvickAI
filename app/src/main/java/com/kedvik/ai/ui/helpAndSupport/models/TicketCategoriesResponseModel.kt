package com.kedvik.ai.ui.helpAndSupport.models

import com.google.gson.annotations.SerializedName

data class TicketCategoriesResponseModel(

	@field:SerializedName("data")
	val data: MutableList<TicketCategoriesDataItemModel>,

	@field:SerializedName("action")
	val action: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class TicketCategoriesDataItemModel(

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("name")
	val name: String
)
