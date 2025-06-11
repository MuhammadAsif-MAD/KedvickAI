package com.kedvik.ai.model

import com.google.gson.annotations.SerializedName

data class FavouriteUnFavouriteResponseModel(

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("action")
	val action: String? = null,

	@SerializedName("status")
	val statusValue: String? = null
)