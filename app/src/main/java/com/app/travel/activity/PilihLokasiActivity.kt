package com.app.travel.activity

//noinspection SuspiciousImport

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.travel.databinding.ActivityPilihLokasiBinding
import com.app.travel.model.Lokasi
import com.app.travel.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PilihLokasiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPilihLokasiBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPilihLokasiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        lokasiListRequest()

        binding.btnLihatJadwal.setOnClickListener {
            if (binding.lokasiTujuan.text.toString() == "" || binding.lokasiKeberangkatan.text.toString() == "") {
                Toast.makeText(
                    this, "Lokasi tujuan dan keberangkatan wajib diisi", Toast.LENGTH_SHORT
                ).show()
            }
            else if (binding.lokasiTujuan.text.toString() == binding.lokasiKeberangkatan.text.toString()) {
                Toast.makeText(
                    this, "Lokasi tujuan dan keberangkatan Tidak boleh sama", Toast.LENGTH_SHORT
                ).show()
            } else {
                val bundle = Bundle()
                bundle.putString("lokasi_tujuan", binding.lokasiTujuan.text.toString())
                bundle.putString("lokasi_keberangkatan", binding.lokasiKeberangkatan.text.toString())
                val intent = Intent(this, JadwalActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun lokasiListRequest() {
        RetrofitService.create(this).lokasiList().enqueue(object : Callback<Lokasi> {
            override fun onResponse(call: Call<Lokasi>, response: Response<Lokasi>) {
                if (response.isSuccessful) {
                    val data = response.body()!!.data!!
                    var feelings = arrayOf<String>()

                    data.forEach {
                        feelings += it!!.nama.toString()
                    }

                    val arrayAdapter = ArrayAdapter(
                        this@PilihLokasiActivity, R.layout.simple_spinner_dropdown_item, feelings
                    )

                    binding.lokasiKeberangkatan.setAdapter(arrayAdapter)
                    binding.lokasiTujuan.setAdapter(arrayAdapter)

                    binding.layout1.isVisible = true
                    binding.layout2.isVisible = true
                    binding.progressBar2.visibility = View.GONE

                }
            }

            override fun onFailure(call: Call<Lokasi>, t: Throwable) {
                Toast.makeText(this@PilihLokasiActivity, "" + t, Toast.LENGTH_SHORT).show()
            }
        })


    }
}