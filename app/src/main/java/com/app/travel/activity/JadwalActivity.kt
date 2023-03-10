package com.app.travel.activity

import android.R
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.travel.adapter.JadwalAdapter
import com.app.travel.databinding.ActivityJadwalBinding
import com.app.travel.model.DataItemJadwal
import com.app.travel.model.Jadwal
import com.app.travel.network.RetrofitService
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JadwalActivity : AppCompatActivity() ,JadwalAdapter.OnItemClickListener  {
    private lateinit var binding: ActivityJadwalBinding
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadwalBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val bundle = intent.extras

        jadwalRequest(bundle!!.getString("lokasi_tujuan")!!, bundle.getString("lokasi_keberangkatan")!!)
        binding.tvLokasi.text = "Perjalanan : "+ bundle.getString("lokasi_keberangkatan")+" -> "+bundle.getString("lokasi_tujuan")
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
                    binding.progressBar3.isVisible = false
                    val data = response.body()!!.data!!
                    val adapter = JadwalAdapter(
                       data,
                        this@JadwalActivity, this@JadwalActivity
                    )
                    binding.rvJadwal.adapter = adapter

                }
            }

            override fun onFailure(call: Call<Jadwal>, t: Throwable) {
                binding.progressBar3.isVisible = false
                Toast.makeText(this@JadwalActivity, "" + t, Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onItemClickedLayananSyarat(item: DataItemJadwal?, s: String) {

        if(s == "pesan"){
            if(item?.status =="penuh"){
                MaterialAlertDialogBuilder(this@JadwalActivity)
                    .setTitle("Maaf Kursi Sudah Penuh")
                    .setNegativeButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }else{
                val bundle = Bundle()
                bundle.putString("id_jadwal", item!!.id.toString())
                Log.i("id_jadwal", "onItemClickedLayananSyarat:"+ item.id.toString())
                val intent = Intent(this, PesanKursiActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
        if(s=="review"){
            val bundle = Bundle()
            bundle.putString("id_mobil", item!!.mobilId.toString())
            val intent = Intent(this, ReviewActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }


    }
}