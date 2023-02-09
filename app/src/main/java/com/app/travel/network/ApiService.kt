package com.app.travel.network

import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("api/user/logout")
    @FormUrlEncoded
    suspend fun logout(@Field("token_firebase") token_firebase: String?,): BaseResponseApi

//    @GET("api/user/detail")
//    suspend fun userDetail(): User

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

//    @POST("api/user/login")
//    @FormUrlEncoded
//    suspend fun login(
//        @Field("username") username: String?,
//        @Field("password") password: String?
//    ): Login

    @Multipart
    @POST("api/user/register")
    suspend fun register(
        @PartMap() partMap: MutableMap<String, RequestBody>,
    ): BaseResponseApi

}