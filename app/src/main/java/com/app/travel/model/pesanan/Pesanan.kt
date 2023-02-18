package com.app.travel.model.pesanan

import com.google.gson.annotations.SerializedName

data class Pesanan(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)