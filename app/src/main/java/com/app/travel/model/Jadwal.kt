package com.app.travel.model

import com.google.gson.annotations.SerializedName

data class Jadwal(

	@field:SerializedName("data")
	val data: List<DataItemJadwal?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataItemJadwal(

	@field:SerializedName("lokasi_keberangkatan")
	val lokasiKeberangkatan: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("lokasi_tujuan_r")
	val lokasiTujuanR: LokasiTujuanR? = null,

	@field:SerializedName("mobil")
	val mobil: Mobil? = null,

	@field:SerializedName("status")
	val status: String? = null,

	@field:SerializedName("kursi_tersedia")
	val kursiTersedia: String? = null,

	@field:SerializedName("lokasi_keberangkatan_r")
	val lokasiKeberangkatanR: LokasiKeberangkatanR? = null,

	@field:SerializedName("harga")
	val harga: Int? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("jam")
	val jam: String? = null,

	@field:SerializedName("supir_id")
	val supirId: Int? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("tanggal")
	val tanggal: String? = null,

	@field:SerializedName("lokasi_tujuan")
	val lokasiTujuan: Int? = null,

	@field:SerializedName("mobil_id")
	val mobilId: Int? = null
)

data class LokasiKeberangkatanR(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)

data class Mobil(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("foto")
	val foto: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("supir_id")
	val supirId: Int? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("kolom_kursi")
	val kolomKursi: Int? = null,

	@field:SerializedName("plat")
	val plat: String? = null,

	@field:SerializedName("type")
	val type: Any? = null
)

data class LokasiTujuanR(

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)
