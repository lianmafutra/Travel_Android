package com.app.travel.model.pesanan

import com.google.gson.annotations.SerializedName

data class Jadwal(

    @field:SerializedName("lokasi_keberangkatan") val lokasiKeberangkatan: Int? = null,

    @field:SerializedName("created_at") val createdAt: String? = null,

    @field:SerializedName("lokasi_tujuan_r") val lokasiTujuanR: LokasiTujuanR? = null,

    @field:SerializedName("kursi_tersedia") val kursiTersedia: String? = null,

    @field:SerializedName("lokasi_keberangkatan_r") val lokasiKeberangkatanR: LokasiKeberangkatanR? = null,

    @field:SerializedName("harga") val harga: Int? = null,

    @field:SerializedName("updated_at") val updatedAt: String? = null,

    @field:SerializedName("jam") val jam: String? = null,

    @field:SerializedName("supir_id") val supirId: Int? = null,

    @field:SerializedName("id") val id: Int? = null,

    @field:SerializedName("tanggal") val tanggal: String? = null,

    @field:SerializedName("lokasi_tujuan") val lokasiTujuan: Int? = null,

    @field:SerializedName("mobil_id") val mobilId: Int? = null,

    @field:SerializedName("status") val status: String? = null,


    @field:SerializedName("jenis_pesanan") val jenisPesanan: String? = null,

    @field:SerializedName("tour_brosur") val tourBrosur: String? = null,

    @field:SerializedName("tour_judul") val tourJudul: String? = null,

    @field:SerializedName("tour_min_orang") val tourMinOrang: String? = null,

    @field:SerializedName("tour_deskripsi") val tourDesc: String? = null,

    @field:SerializedName("tour_dp") val tourDP: Int? = null,
)