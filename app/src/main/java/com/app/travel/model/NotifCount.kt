package com.app.travel.model

import com.google.gson.annotations.SerializedName

data class NotifCount(

	@field:SerializedName("data")
	val data: Int? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
