package com.app.travel.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.R
import com.app.travel.databinding.ActivityMainBinding
import com.app.travel.model.UserDetail
import com.app.travel.network.Config
import com.app.travel.network.RetrofitService
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.card1.setOnClickListener {
            startActivity(Intent(this, PilihLokasiActivity::class.java))
        }

        binding.card2.setOnClickListener {
            startActivity(Intent(this, ProfilActivity::class.java))
        }

        binding.card3.setOnClickListener {
            startActivity(Intent(this, InfoAplikasiActivity::class.java))
        }

        binding.card4.setOnClickListener {
            startActivity(Intent(this, PesananActivity::class.java))
        }

        userDetailRequest()

    }

    override fun onResume() {
        super.onResume()
        userDetailRequest()
    }

    private fun userDetailRequest() {
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loader_circle)
            .error(R.drawable.ic_user)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform()
        RetrofitService.create(this).userDetail().enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                if (response.isSuccessful) {
                    val data = response.body()!!.data!!
                    binding.tvNama.text = data.namaLengkap.toString()
                    Glide.with(this@MainActivity).load(Config.URL_STORAGE +data.foto).apply(options).into(binding.imgFoto)
                }
            }

            override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                if (t is IOException) {

                }
                Toast.makeText(this@MainActivity, "" + t, Toast.LENGTH_SHORT).show()
            }
        })

    }


}