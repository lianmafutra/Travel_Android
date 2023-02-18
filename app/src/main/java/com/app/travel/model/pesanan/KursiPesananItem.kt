package com.app.travel.model.pesanan

import com.google.gson.annotations.SerializedName

data class KursiPesananItem(

	@field:SerializedName("kursi_mobil_id")
	val kursiMobilId: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("pesanan_id")
	val pesananId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)