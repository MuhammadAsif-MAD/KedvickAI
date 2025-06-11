package com.kedvik.ai.ui.helpAndSupport.models

import com.google.gson.annotations.SerializedName

data class SendTicketMessageResponseModel(

	@field:SerializedName("result")
	val result: Boolean? = null,

	@field:SerializedName("data")
	val data: SendTicketMessageDataModel? = null,

	@field:SerializedName("action")
	val action: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Post(
	val any: Any? = null
)

data class SendTicketMessageDataModel(
	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("is_read")
	val isRead: Int? = null,

	@field:SerializedName("attachment")
	val attachment: String? = null,

	@field:SerializedName("post_id")
	val postId: String? = null,

	@field:SerializedName("post")
	val post: Post? = null,

	@field:SerializedName("type")
	val attachmentType: String? = null,

	@field:SerializedName("channel")
	val channel: String? = null,

	@field:SerializedName("from")
	val from: String? = null,

	@field:SerializedName("to")
	val to: String? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("ticket_id")
	val ticketId: String? = null
)
