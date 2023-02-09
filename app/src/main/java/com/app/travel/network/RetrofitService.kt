package com.app.travel.network

import android.content.Context
import android.content.Intent
import com.app.travel.LoginActiviry
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitService {

    private lateinit var apiService: ApiService

    fun getApiService(context: Context): ApiService {
        if (!::apiService.isInitialized) {

            val retrofit = Retrofit.Builder()
                .baseUrl("")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient(context))
                .build()
            apiService = retrofit.create(ApiService::class.java)

        }
        return apiService
    }

    private fun okHttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(Interceptor { chain ->
            val request: Request = chain.request()
            val response = chain.proceed(request)
            if (response.code == 401) {
                val intent = Intent(context, LoginActiviry::class.java)
                context.startActivity(intent)
            }
            response
        }).addInterceptor(AuthInterceptor(context))
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
    }

}
