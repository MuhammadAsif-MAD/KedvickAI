package com.kedvick.ai.ui.helpAndSupport.models

import com.google.gson.annotations.SerializedName

data class TicketDetailResponseModel(

	@field:SerializedName("data")
	val data: MutableList<TicketDetailDataItemModel>,

	@field:SerializedName("action")
	val action: String? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class TicketDetailDataItemModel(

	@field:SerializedName("from")
	val from: String? = null,

	@field:SerializedName("type")
	val attachmentType: String? = null,

	@field:SerializedName("time")
	val time: String? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("is_read")
	val isRead: Int? = null,

	@field:SerializedName("attachment")
	val attachment: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("ticket_id")
	val ticketId: Int? = null,

	@field:SerializedName("to")
	val to: String? = null
)
