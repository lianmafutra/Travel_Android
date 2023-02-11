package com.app.travel.model

import com.google.gson.annotations.SerializedName

data class UserDetail(

	@field:SerializedName("data")
	val data: UserDetailData? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class UserDetailData(

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
