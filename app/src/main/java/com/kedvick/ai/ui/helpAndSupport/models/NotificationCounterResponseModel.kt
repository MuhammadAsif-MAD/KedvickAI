package com.kedvick.ai.ui.helpAndSupport.models

import com.google.gson.annotations.SerializedName
import com.kedvick.ai.model.ErrorsItem

data class NotificationCounterResponseModel(

	@field:SerializedName("data")
	val data: NotificationCounterDataModel? = null,

	@field:SerializedName("action")
	val action: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("errors")
	val errors: List<ErrorsItem?>? = null,

	@field:SerializedName("status")
	val status: Boolean? = null
)

data class NotificationCounterDataModel(

	@field:SerializedName("ticket_unread_counter")
	val ticketUnreadCounter: Int? = null
)

