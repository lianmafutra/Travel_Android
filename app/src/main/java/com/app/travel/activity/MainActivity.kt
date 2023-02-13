package com.app.travel.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.app.travel.databinding.ActivityMainBinding
import com.app.travel.model.UserDetail
import com.app.travel.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.card1.setOnClickListener{
            startActivity(Intent(this, PilihLokasiActivity::class.java))
        }

        binding.card2.setOnClickListener{
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        binding.card3.setOnClickListener{
            startActivity(Intent(this, InfoAplikasiActivity::class.java))
        }

        binding.card4.setOnClickListener{
            startActivity(Intent(this, PesananActivity::class.java))
        }

        userDetailRequest()

    }

    private fun userDetailRequest() {
        RetrofitService.create(this).userDetail().enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                if (response.isSuccessful) {
                    val data = response.body()!!.data!!
                    binding.tvNama.text = data.namaLengkap.toString()
                }
            }
            override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                if (t is IOException) {

                }
                Toast.makeText(this@MainActivity, ""+ t, Toast.LENGTH_SHORT).show()
            }
        })

    }


}