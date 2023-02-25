package com.app.travel.network

import com.app.travel.model.*
import com.app.travel.model.pesanan.Pesanan
import okhttp3.RequestBody
import retrofit2.Call
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


    @POST("api/user/token/fcm")
    @FormUrlEncoded
    fun insertTokenFCM( @Field("token") token: String?) : Call<BaseResponseApi>

    @GET("api/pesanan/detail/{kode_pesanan}")
    fun pesananDetail(@Path("kode_pesanan") kode_pesanan: String) : Call<Pesanan>

    @POST("api/pesanan/detail/upload_bukti")
    fun uploadBukti(@Body body: RequestBody): Call<BaseResponseApi>

    @POST("api/user/register")
    fun registerUser(@Body body: RequestBody
    ): Call<BaseResponseApi>

    @POST("api/pesanan/review")
    fun kirimReview(@Body body: RequestBody
    ): Call<BaseResponseApi>

    @POST("api/user/pesanan")
    fun buatPesanan(@Body body: RequestBody
    ): Call<BaseResponseApi>

    @GET("api/user/pesanan/list")
    fun pesananRequest() : Call<Pesanan>

    @POST("api/user/password/update")
    fun updatePassword(@Body body: RequestBody
    ): Call<BaseResponseApi>



    @GET("api/jadwal/detail/{id_jadwal}")
    fun jadwalDetail(@Query("id_jadwal") id_jadwal: String) : Call<Jadwal>

    @GET("/api/pesanan/mobil/{id_mobil}/review")
    fun reviewByMobil(@Path("id_mobil") id_mobil: String) : Call<ReviewPengguna>

//    @GET("api/jadwal/detail/{id_jadwal}")
//    fun jadwalDetail(@Query("id_jadwal") id_jadwal: String) : Call<Jadwal>

    @POST("api/user/logout")
    @FormUrlEncoded
     fun logout(@Field("token_firebase") token_firebase: String?): Call<BaseResponseApi>


    @GET("/api/pesanan/notif/count")
    fun notifCount() : Call<NotifCount>


    @POST("api/user/password/lupa")
    @FormUrlEncoded
     fun lupaPassword(
        @Field("nik") nik: String?,
        @Field("kk") kk: String?,
        @Field("email") email: String?,
        @Field("password_baru") password_baru: String?
    ): Call<BaseResponseApi>

    @POST("api/user/profil/update")
    @FormUrlEncoded
     fun updateProfil(
        @Field("nama_lengkap") nama_lengkap: String?,
        @Field("email") email: String?,
        @Field("kontak") kontak: String?,
        @Field("alamat") alamat: String?,
        @Field("jenis_kelamin") jenis_kelamin: String?,
    ): Call<BaseResponseApi>


    @POST("api/user/profil/foto/update")
    fun updateFoto(@Body body: RequestBody): Call<BaseResponseApi>

    @POST("api/user/password/ubah")
    @FormUrlEncoded
     fun ubahPassword(
        @Field("password_lama") password_lama: String?,
        @Field("password_baru") password_baru: String?
    ): Call<BaseResponseApi>

    @Multipart
    @POST("api/user/register")
     fun register(
        @PartMap() partMap: MutableMap<String, RequestBody>,
    ): Call<BaseResponseApi>

}