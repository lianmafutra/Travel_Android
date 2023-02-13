package com.app.travel.network

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.app.travel.activity.LoginActivity
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


class NetworkConnectionInterceptor(context: Context) : Interceptor {

    val data  = context;

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        try {
            val response = chain.proceed(request)

            val bodyString = response.body!!.string()

            if (response.code == 401){
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(data, "401 : Token Tidak Valid, Silahkan login kembali", Toast.LENGTH_SHORT).show()
                    data.startActivity(Intent(data, LoginActivity::class.java))
                }
            }
            if (response.code == 301){
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(data, "301 : Time out", Toast.LENGTH_SHORT).show()
                }
            }
            if (response.code == 404) {
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(data, "404 : Resource tidak dapat ditemukan", Toast.LENGTH_SHORT)
                        .show()
                    data.startActivity(Intent(data, LoginActivity::class.java))
                }
            }
            if (response.code == 500){
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(data, "500 : Server error", Toast.LENGTH_SHORT).show()
                }
            }

            return response.newBuilder()
                .body(ResponseBody.create(response.body?.contentType(), bodyString))
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            var msg = ""

            when (e) {
                is SocketTimeoutException -> {
                    msg = "Timeout - Please check your internet connection"
                }
                is UnknownHostException -> {
                    msg = "Unable to make a connection. Please check your internet"
                }
                is ConnectionShutdownException -> {
                    msg = "Connection shutdown. Please check your internet"
                }
                is IOException -> {
                    msg = "Server is unreachable, please try again later."
                }
                is IllegalStateException -> {
                    msg = "${e.message}"
                }
                else -> {
                    msg = "${e.message}"
                }
            }
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(data, "error : $msg", Toast.LENGTH_SHORT).show()
            }

            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(999)
                .message(msg)
                .body(ResponseBody.create(null, "{${e}}")).build()
        }
    }
}