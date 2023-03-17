package com.app.travel.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.databinding.ActivityConfigIpBinding
import com.app.travel.model.Login
import com.app.travel.network.ApiService
import com.app.travel.network.RetrofitService
import com.app.travel.network.SessionManager
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class ConfigIpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigIpBinding
    private lateinit var sessionManager: SessionManager
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigIpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sessionManager = SessionManager(this)
        binding.tvIp.text = "Server Running IP :"+sessionManager.getIPServer()

        binding.btnLogin2.setOnClickListener {
            sessionManager.saveIPServer("http://"+binding.edtServerIp.text.toString())
            Toast.makeText(this, "Alamat IP Server  Berhasil Diterapkan", Toast.LENGTH_SHORT).show()
            binding.tvIp.text = "Server Running IP :"+sessionManager.getIPServer()
        }

        binding.btnCekKoneksi.setOnClickListener{
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("http://"+binding.edtServerIp.text.toString())
                .build()

            try {
               retrofit.create(ApiService::class.java).login("testing", "123").enqueue(object :
                    Callback<Login> {
                    override fun onResponse(call: Call<Login>, response: Response<Login>) {
                        if (response.body()?.success!!) {
                            startActivity(Intent(this@ConfigIpActivity, MainActivity::class.java))
                            sessionManager.saveAuthToken(response.body()!!.data!!.token.toString())
                            sessionManager.saveUser(response.body()!!.data!!.user)
                        } else {
                            Toast.makeText(this@ConfigIpActivity, "Koneksi Berhasil, lanjutkan simpan", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Login>, t: Throwable) {
                        Toast.makeText(this@ConfigIpActivity, "" + t, Toast.LENGTH_SHORT).show()
                    }
                })
            }catch (t : Throwable){
                Toast.makeText(this@ConfigIpActivity, "" + t, Toast.LENGTH_SHORT).show()
            }

        }


    }
}