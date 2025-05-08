package com.kedvick.ai.model

import com.google.gson.annotations.SerializedName

data class UserResponseModel(

	@field:SerializedName("data")
	val data: UserDataModel? = null,

	@field:SerializedName("action")
	val action: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("errors")
	val errorsList: List<ErrorsItem?>? = null
)

data class UserDataModel(

	@field:SerializedName("current_plan")
	val currentPlan: String? = null,

	@field:SerializedName("user_monthly_usage_image")
	val userMonthlyUsageImage: List<Int?>? = null,

	@field:SerializedName("template_used")
	val templateUsed: Int? = null,

	@field:SerializedName("available_image")
	var availableImage: String? = null,

	@field:SerializedName("emailId")
	var emailId: String? = null,

	@field:SerializedName("profileImage")
	var image: String? = null,

	@field:SerializedName("user_monthly_usage_word")
	val userMonthlyUsageWord: List<Int?>? = null,

	@field:SerializedName("userName")
	var userName: String? = null,

	@field:SerializedName("favorite_template")
	val favoriteTemplate: List<Any?>? = null,

	@field:SerializedName("userId")
	var userId: String? = null,

	@field:SerializedName("isSubscribe")
	var isSubscribe: Boolean? = null,

	@field:SerializedName("available_word")
	var availableWord: String? = null,

	@field:SerializedName("document_created")
	val documentCreated: Int? = null,

	@field:SerializedName("referral_code")
	var referralCode: String? = null,

	@field:SerializedName("image_created")
	val imageCreated: Int? = null,

	@field:SerializedName("referred_by")
	var referredBy: Int? = null,

	@field:SerializedName("password_added")
	var passwordAdded: Int? = null,

	@field:SerializedName("word_used")
	var wordUsed: Int? = null,

	@field:SerializedName("start_date")
	val startDate: Any? = null,

	@SerializedName("action")
	val action: String? = null,

	@SerializedName("token")
	var token: String? = null,

	@field:SerializedName("status")
	val status: Int? = null
)
