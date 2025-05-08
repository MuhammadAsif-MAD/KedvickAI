package com.kedvick.ai.ui.helpAndSupport.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class ItemSupportReq (
    @field:SerializedName("ticketId")
    var ticketId: String? = null,

    @field:SerializedName("status")
    var status: String? = null,

    @field:SerializedName("category")
    var category: String? = null,

    @field:SerializedName("subject")
    var subject: String? = null,

    @field:SerializedName("priority")
    var priority: String? = null,

    @field:SerializedName("lastUpdated")
    var lastUpdated: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ticketId)
        parcel.writeString(status)
        parcel.writeString(category)
        parcel.writeString(subject)
        parcel.writeString(priority)
        parcel.writeString(lastUpdated)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemSupportReq> {
        override fun createFromParcel(parcel: Parcel): ItemSupportReq {
            return ItemSupportReq(parcel)
        }

        override fun newArray(size: Int): Array<ItemSupportReq?> {
            return arrayOfNulls(size)
        }
    }
}
