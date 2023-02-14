package com.app.travel.model

import com.google.gson.annotations.SerializedName

data class ReviewPengguna(

	@field:SerializedName("data")
	val data: List<DataItemReview?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class UserReview(

	@field:SerializedName("kontak")
	val kontak: String? = null,

	@field:SerializedName("foto")
	val foto: String? = null,

	@field:SerializedName("updated_at")
	val updatedAt: String? = null,

	@field:SerializedName("nama_lengkap")
	val namaLengkap: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("jenis_kelamin")
	val jenisKelamin: Any? = null,

	@field:SerializedName("email")
	val email: Any? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("alamat")
	val alamat: String? = null
)

data class DataItemReview(

	@field:SerializedName("kode_pesanan")
	val kodePesanan: String? = null,

	@field:SerializedName("rating_komen")
	val ratingKomen: String? = null,

	@field:SerializedName("created_at")
	val createdAt: String? = null,

	@field:SerializedName("rating_nilai")
	val ratingNilai: Float?,

	@field:SerializedName("status_pesanan")
	val statusPesanan: String? = null,

	@field:SerializedName("tgl_pembayaran")
	val tglPembayaran: Any? = null,

	@field:SerializedName("kontak")
	val kontak: String? = null,

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

	@field:SerializedName("supir_id")
	val supirId: Any? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("user")
	val user: UserReview? = null,

	@field:SerializedName("bukti_pembayaran")
	val buktiPembayaran: String? = null,

	@field:SerializedName("mobil_id")
	val mobilId: Int? = null
)
