package com.kedvick.ai.ui.helpAndSupport.models

import com.google.gson.annotations.SerializedName

data class SupportDetails (
    @field:SerializedName("ticket_id")
    var ticketId: String? = null,

    @field:SerializedName("subject")
    var subject: String? = null,

    @field:SerializedName("status")
    var status: String? = null,

    @field:SerializedName("message")
    var message: MutableList<Message>

)