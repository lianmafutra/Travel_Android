package com.app.travel.model

import com.google.gson.annotations.SerializedName

 data class Lokasi(

	@field:SerializedName("data")
	val data: List<DataItemLokasi?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

 data class DataItemLokasi(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

)

