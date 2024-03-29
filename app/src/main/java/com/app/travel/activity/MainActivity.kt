package com.app.travel.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.travel.R
import com.app.travel.databinding.ActivityMainBinding
import com.app.travel.model.NotifCount
import com.app.travel.model.UserDetail
import com.app.travel.network.BaseResponseApi
import com.app.travel.network.RetrofitService
import com.app.travel.network.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sessionManager = SessionManager(this)

        binding.card1.setOnClickListener {

            val options = arrayOf("Travel", "Tour Wisata")
            val builder3 = MaterialAlertDialogBuilder(this)
                .setTitle("Pilih Jenis : ")
                .setItems(options) { dialog, which ->
                    // Handle option selection here
                    when (which) {
                        0 -> {
                            startActivity(Intent(this, PilihLokasiActivity::class.java))
                        }
                        1 -> {
                            startActivity(Intent(this, JadwalTourActivity::class.java))
                        }
                    }
                }
                .setNegativeButton("Batal") { dialog, which ->
                    dialog.dismiss()
                }

            val dialog3 = builder3.create()
            dialog3.show()
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
        notifCountRequest()
        insertTokenUser()

        binding.swiperefresh.setOnRefreshListener {
            userDetailRequest()
            notifCountRequest()
            insertTokenUser()
        }

    }

    override fun onResume() {
        super.onResume()
        userDetailRequest()
        notifCountRequest()
    }

    private fun notifCountRequest() {
        RetrofitService.create(this).notifCount().enqueue(object : Callback<NotifCount> {
            override fun onResponse(call: Call<NotifCount>, response: Response<NotifCount>) {
                if (response.isSuccessful) {
                    val data = response.body()!!.data!!
                    if (data > 0) {
                        binding.tvNotifCount.isVisible = true
                        binding.tvNotifCount.text = data.toString()
                    }else{
                        binding.tvNotifCount.isVisible = false

                    }
                }
            }

            override fun onFailure(call: Call<NotifCount>, t: Throwable) {
                Toast.makeText(this@MainActivity, "" + t, Toast.LENGTH_SHORT).show()
            }
        })

    }



    private fun insertTokenUser() {
        if (sessionManager.getFCMToken() == null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Toast.makeText(this, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    return@OnCompleteListener
                }
                val token = task.result
                RetrofitService.create(this).insertTokenFCM(token)
                    .enqueue(object : Callback<BaseResponseApi> {
                        override fun onResponse(
                            call: Call<BaseResponseApi>, response: Response<BaseResponseApi>
                        ) {
                            if (response.body()?.success!!) {
                                sessionManager.saveFCMToken(token)
                                Toast.makeText(
                                    this@MainActivity, response.body()!!.message, Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@MainActivity, response.body()!!.message, Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<BaseResponseApi>, t: Throwable) {
                            Toast.makeText(this@MainActivity, "" + t, Toast.LENGTH_SHORT).show()
                        }
                    })
            })
        }

    }


    private fun userDetailRequest() {
        val options: RequestOptions =
            RequestOptions().centerCrop().placeholder(R.drawable.loader_circle)
                .error(R.drawable.ic_user).diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH).dontAnimate().dontTransform()

        RetrofitService.create(this).userDetail().enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                if (response.isSuccessful) {
                    binding.swiperefresh.isRefreshing = false
                    val data = response.body()!!.data!!
                    binding.tvNama.text = data.namaLengkap.toString()
                    Glide.with(this@MainActivity).load(sessionManager.getIPServer()+"/storage/" + data.foto)
                        .apply(options).into(binding.imgFoto)
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