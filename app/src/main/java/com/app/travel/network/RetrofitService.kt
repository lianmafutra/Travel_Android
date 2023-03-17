package com.app.travel.network

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.app.travel.activity.LoginActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


object RetrofitService {

    fun create(context: Context): ApiService {



        val mOkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(NetworkConnectionInterceptor(context))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()

        val sessionManager = SessionManager(context)

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(sessionManager.getIPServer().toString())
            .client(mOkHttpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
