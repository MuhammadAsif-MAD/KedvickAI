package com.kedvick.ai.model

import com.google.gson.annotations.SerializedName

data class BaseResponseModel(

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("action")
	val action: String? = null,

	@SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("errors")
	val errorsList: List<ErrorsItem?>? = null,
)

data class ErrorsItem(

	@field:SerializedName("field")
	val field: String? = null,

	@field:SerializedName("message")
	val message: String? = null
)