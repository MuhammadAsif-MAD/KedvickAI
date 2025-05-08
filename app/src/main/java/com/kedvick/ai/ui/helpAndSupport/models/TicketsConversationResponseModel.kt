package com.kedvick.ai.ui.helpAndSupport.models

import com.google.gson.annotations.SerializedName

data class TicketsConversationResponseModel(

	@field:SerializedName("data")
	val data: MutableList<TicketsssConversationDataItemModel>,

	@field:SerializedName("action")
	val action: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class TicketsssssConversationUserModel(

	@field:SerializedName("password_added")
	val passwordAdded: Int? = null,

	@field:SerializedName("mobile_no")
	val mobileNo: Any? = null,

	@field:SerializedName("oauth_type")
	val oauthType: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("platform")
	val platform: String? = null,

	@field:SerializedName("subscription_id")
	val subscriptionId: Any? = null,

	@field:SerializedName("user_type")
	val userType: String? = null,

	@field:SerializedName("is_deleted")
	val isDeleted: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("image_left")
	val imageLeft: Int? = null,

	@field:SerializedName("subscription_start_date")
	val subscriptionStartDate: Any? = null,

	@field:SerializedName("fcm_token")
	val fcmToken: Any? = null,

	@field:SerializedName("oauth_id")
	val oauthId: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("image")
	val image: Any? = null,

	@field:SerializedName("email_verified_at")
	val emailVerifiedAt: String? = null,

	@field:SerializedName("words_left")
	val wordsLeft: Int? = null,

	@field:SerializedName("subscription_end_date")
	val subscriptionEndDate: Any? = null,

	@field:SerializedName("referral_code")
	val referralCode: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("platform_id")
	val platformId: String? = null,

	@field:SerializedName("referred_by")
	val referredBy: Int? = null,

	@field:SerializedName("is_subscribe")
	val isSubscribe: Any? = null,

	@field:SerializedName("status")
	val status: Int? = null
)

data class TicketsssConversationDataItemModel(

	@field:SerializedName("is_read")
	val isRead: Int? = null,

	@field:SerializedName("attachment")
	val attachment: String? = null,

	@field:SerializedName("from")
	val from: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("to")
	val to: Int? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("ticket_id")
	val ticketId: Int? = null,

	@field:SerializedName("type")
	val type: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("user")
	val user: TicketsssssConversationUserModel? = null
)
