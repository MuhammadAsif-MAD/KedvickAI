package com.kedvik.ai.ui.helpAndSupport.models

import com.google.gson.annotations.SerializedName

data class FAQsResponseModel(
	@field:SerializedName("data")
	val data: List<FAQsDataModel>,

	@field:SerializedName("action")
	val action: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)
data class FAQsDataModel(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("question")
	val question: String? = null,

	@field:SerializedName("answer")
	val answer: String? = null,

	@field:SerializedName("created_at")
	val created_at: String? = null,

	@field:SerializedName("updated_at")
	val updated_at: String? = null,

	var isChecked : Boolean
)