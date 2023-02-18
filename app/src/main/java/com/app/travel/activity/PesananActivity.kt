package com.app.travel.activity

//noinspection SuspiciousImport
import android.R
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.travel.adapter.JadwalAdapter
import com.app.travel.adapter.PesananAdapter
import com.app.travel.databinding.ActivityPesananBinding
import com.app.travel.model.Jadwal
import com.app.travel.model.pesanan.DataItem
import com.app.travel.model.pesanan.Pesanan
import com.app.travel.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PesananActivity : AppCompatActivity(),PesananAdapter.OnItemClickListener {
    private lateinit var binding: ActivityPesananBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val bundle = intent.extras
        pesananRequest()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun pesananRequest(){
        binding.rvPesanan.layoutManager = LinearLayoutManager(this)
        binding.rvPesanan.adapter = null
        RetrofitService.create(this).pesananRequest().enqueue(object :
            Callback<Pesanan> {
            override fun onResponse(call: Call<Pesanan>, response: Response<Pesanan>) {
                if (response.isSuccessful) {
                    val data = response.body()!!.data!!
                    val adapter = PesananAdapter(
                        data,
                        this@PesananActivity, this@PesananActivity
                    )
                    binding.rvPesanan.adapter = adapter

                }
            }

            override fun onFailure(call: Call<Pesanan>, t: Throwable) {
                Toast.makeText(this@PesananActivity, "" + t, Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun onItemClickedLayananSyarat(item: DataItem?) {
        Toast.makeText(this, ""+item, Toast.LENGTH_SHORT).show()
    }
}