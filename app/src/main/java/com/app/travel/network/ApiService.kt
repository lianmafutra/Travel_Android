package com.app.travel.network

import com.app.travel.model.Jadwal
import com.app.travel.model.Login
import com.app.travel.model.Lokasi
import com.app.travel.model.UserDetail
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/user/login")
    @FormUrlEncoded
     fun login(
        @Field("username") username: String?,
        @Field("password") password: String?
    ): Call<Login>


    @GET("api/user/detail")
     fun userDetail() : Call<UserDetail>


    @GET("api/lokasi")
    fun lokasiList() : Call<Lokasi>

    @POST("api/jadwal/lokasi")
    @FormUrlEncoded
    fun jadwalByLokasi(  @Field("lokasi_tujuan") lokasi_tujuan: String?,
                         @Field("lokasi_keberangkatan") lokasi_keberangkatan: String?) : Call<Jadwal>

    @GET("api/jadwal/detail/{id_jadwal}")
    fun jadwalDetail(@Query("id_jadwal") id_jadwal: String) : Call<Jadwal>

//    @GET("api/jadwal/detail/{id_jadwal}")
//    fun jadwalDetail(@Query("id_jadwal") id_jadwal: String) : Call<Jadwal>

    @POST("api/user/logout")
    @FormUrlEncoded
    suspend fun logout(@Field("token_firebase") token_firebase: String?,): BaseResponseApi


    @POST("api/user/password/lupa")
    @FormUrlEncoded
    suspend fun lupaPassword(
        @Field("nik") nik: String?,
        @Field("kk") kk: String?,
        @Field("email") email: String?,
        @Field("password_baru") password_baru: String?
    ): BaseResponseApi

    @POST("api/user/data/ubah")
    @FormUrlEncoded
    suspend fun editData(
        @Field("email") email: String?,
        @Field("no_hp") no_hp: String?,
    ): BaseResponseApi

    @POST("api/user/password/ubah")
    @FormUrlEncoded
    suspend fun ubahPassword(
        @Field("password_lama") password_lama: String?,
        @Field("password_baru") password_baru: String?
    ): BaseResponseApi

    @Multipart
    @POST("api/user/register")
    suspend fun register(
        @PartMap() partMap: MutableMap<String, RequestBody>,
    ): BaseResponseApi

}