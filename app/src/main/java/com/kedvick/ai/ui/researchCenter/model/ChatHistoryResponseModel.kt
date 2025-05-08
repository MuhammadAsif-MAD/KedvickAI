package com.kedvick.ai.ui.researchCenter.model

import com.google.gson.annotations.SerializedName

data class ChatHistoryResponseModel(

	@field:SerializedName("records")
	val records: MutableList<ChatHistoryRecordsItemModel>,

	@field:SerializedName("chats_count")
	val chatsCount: Int? = null
)

data class ChatDataItemModel(

	@field:SerializedName("role")
	val role: String? = null,

	@field:SerializedName("chatId")
	val chatId: String? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("media")
	val media: String? = null,

	@field:SerializedName("pdf")
	val pdf: String? = null
)

data class ChatHistoryRecordsItemModel(

	@field:SerializedName("chatId")
	val chatId: String? = null,

	@field:SerializedName("data")
	val data: MutableList<ChatDataItemModel>
)
