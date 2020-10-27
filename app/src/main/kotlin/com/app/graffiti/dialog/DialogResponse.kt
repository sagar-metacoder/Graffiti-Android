package com.app.graffiti.dialog

import com.google.gson.annotations.SerializedName


data class DialogResponse(
        @SerializedName("status")
        val status: Int,
        @SerializedName("message")
        val message: Message,
        @SerializedName("data")
        val data: Data
) {
    data class Message(
            @SerializedName("success")
            val success: String
    )

    data class Data(
            @SerializedName("url")
            val url: String
    )
}