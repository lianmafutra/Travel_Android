package com.app.travel.model.pesanan

import com.google.gson.annotations.SerializedName

data class DataItem(

	@field:SerializedName("kode_pesanan")
	val kodePesanan: String? = null,

	@field:SerializedName("rating_komen")
	val ratingKomen: Any? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,


	@field:SerializedName("pesan_tolak")
	val pesanTolak: String? = null,

	@field:SerializedName("total_biaya")
	val total_biaya: Int? = null,


	@field:SerializedName("id_kursi_pesanan")
	val id_kursi_pesanan: String? = null,

	@field:SerializedName("rating_nilai")
	val ratingNilai: Int? = null,

	@field:SerializedName("status_pesanan")
	val statusPesanan: String? = null,

	@field:SerializedName("tgl_pembayaran")
	val tglPembayaran: String? = null,

	@field:SerializedName("kontak")
	val kontak: String? = null,

	@field:SerializedName("jadwal")
	val jadwal: Jadwal? = null,

	@field:SerializedName("jadwal_id")
	val jadwalId: Int? = null,

	@field:SerializedName("nama")
	val nama: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("user_id")
	val userId: Int? = null,

	@field:SerializedName("status_pembayaran")
	val statusPembayaran: String? = null,

	@field:SerializedName("tgl_keberangkatan")
	val tglKeberangkatan: String? = null,

	@field:SerializedName("tgl_pesan")
	val tglPesan: String? = null,

	@field:SerializedName("kursi_pesanan")
	val kursiPesanan: List<KursiPesananItem?>? = null,

	@field:SerializedName("supir_id")
	val supirId: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("bukti_pembayaran")
	val buktiPembayaran: String? = null,

	@field:SerializedName("mobil_id")
	val mobilId: Int? = null
)