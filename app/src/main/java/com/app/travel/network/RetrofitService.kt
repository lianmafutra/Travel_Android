package com.app.travel.network

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.app.travel.MainActivity
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitService {




    fun create(context: Context): ApiService {
        val handler = Handler(Looper.getMainLooper())
        val mOkHttpClient = OkHttpClient
            .Builder()
            .addInterceptor(AuthInterceptor(context))
            .addInterceptor(Interceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                if (response.code == 401){
                    handler.post {
                        Toast.makeText(context, "401 : Token Tidak Valid", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, MainActivity::class.java))
                    }
                }
                response
            })
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()


        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://192.168.237.131:8000")
            .client(mOkHttpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }





//    fun getInstance(context: Context): Retrofit {
//
//
//        val mOkHttpClient = OkHttpClient
//            .Builder()
//            .addInterceptor(AuthInterceptor(context))
//            .addInterceptor(Interceptor { chain ->
//                val request: Request = chain.request()
//                val response = chain.proceed(request)
//                response
//            })
//
//
//            .connectTimeout(1, TimeUnit.MINUTES)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .retryOnConnectionFailure(false)
//            .build()
//
//
//
//
//        return Retrofit.Builder()
//            .baseUrl("http://192.168.237.131:8000")
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(mOkHttpClient)
//            .build()
//    }

}
