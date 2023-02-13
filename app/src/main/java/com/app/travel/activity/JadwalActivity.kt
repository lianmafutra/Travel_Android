package com.app.travel.activity

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.travel.adapter.JadwalAdapter
import com.app.travel.databinding.ActivityJadwalBinding
import com.app.travel.databinding.ActivityPilihLokasiBinding
import com.app.travel.model.DataItemJadwal
import com.app.travel.model.Jadwal
import com.app.travel.model.Lokasi
import com.app.travel.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JadwalActivity : AppCompatActivity() ,JadwalAdapter.OnItemClickListener  {
    private lateinit var binding: ActivityJadwalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadwalBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val bundle = intent.extras

        jadwalRequest(bundle!!.getString("lokasi_tujuan")!!, bundle.getString("lokasi_keberangkatan")!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun jadwalRequest(lokasi_tujuan: String, lokasi_keberangkatan: String){
        binding.rvJadwal.layoutManager = LinearLayoutManager(this)
        binding.rvJadwal.adapter = null
        RetrofitService.create(this).jadwalByLokasi(lokasi_tujuan, lokasi_keberangkatan).enqueue(object : Callback<Jadwal> {
            override fun onResponse(call: Call<Jadwal>, response: Response<Jadwal>) {
                if (response.isSuccessful) {
                    val data = response.body()!!.data!!
                    val adapter = JadwalAdapter(
                       data,
                        this@JadwalActivity,
                        this@JadwalActivity
                    )
                    binding.rvJadwal.adapter = adapter

                }
            }

            override fun onFailure(call: Call<Jadwal>, t: Throwable) {
                Toast.makeText(this@JadwalActivity, "" + t, Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onItemClickedLayananSyarat(layananItem: DataItemJadwal?) {
        Toast.makeText(this, ""+ layananItem!!.id, Toast.LENGTH_SHORT).show()
    }
}