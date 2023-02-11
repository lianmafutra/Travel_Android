package com.app.travel

import android.accounts.NetworkErrorException
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.app.travel.databinding.ActivityLoginActiviryBinding
import com.app.travel.model.Login
import com.app.travel.model.UserDetail
import com.app.travel.network.ApiService
import com.app.travel.network.RetrofitService
import com.app.travel.network.SessionManager
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okio.IOException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.HTTP


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginActiviryBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginActiviryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sessionManager = SessionManager(this)

        binding.tvDaftarDisini.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            loginRequest(binding.edtEmail1.text.toString(), binding.edtPassword.text.toString())
            userDetailRequest()
        }


    }

    private fun loginRequest(username: String, password : String) {


        RetrofitService.create(this).login(username, password).enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if (response.body()!!.success!!) {
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    sessionManager.saveAuthToken(response.body()!!.data!!.token.toString())
                    Toast.makeText(this@LoginActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()

                }else{
                    Toast.makeText(this@LoginActivity, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "" + t, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun userDetailRequest() {
      RetrofitService.create(this).userDetail().enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                if (response.isSuccessful) {
                    val data = response.body()
                    Toast.makeText(this@LoginActivity, ""+ data?.data.toString(), Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                Toast.makeText(this@LoginActivity, ""+ t, Toast.LENGTH_SHORT).show()
            }
        })

    }
    }
