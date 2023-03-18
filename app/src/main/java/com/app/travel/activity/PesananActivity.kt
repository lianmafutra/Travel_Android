package com.app.travel.activity

//noinspection SuspiciousImport
import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.travel.adapter.PesananAdapter
import com.app.travel.databinding.ActivityPesananBinding
import com.app.travel.model.pesanan.DataItem
import com.app.travel.model.pesanan.Pesanan
import com.app.travel.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PesananActivity : AppCompatActivity(), PesananAdapter.OnItemClickListener {
    private lateinit var binding: ActivityPesananBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        pesananRequest()

        binding.swiperefresh.setOnRefreshListener {
            pesananRequest()
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

    private fun pesananRequest() {
        binding.rvPesanan.layoutManager = LinearLayoutManager(this)
        binding.rvPesanan.adapter = null
        RetrofitService.create(this).pesananRequest().enqueue(object : Callback<Pesanan> {
            override fun onResponse(call: Call<Pesanan>, response: Response<Pesanan>) {
                if (response.isSuccessful) {

                    val data = response.body()!!.data!!
                    val adapter = PesananAdapter(
                        data, this@PesananActivity, this@PesananActivity
                    )
                    binding.rvPesanan.adapter = adapter
                    binding.swiperefresh.isRefreshing = false
                    binding.progressBar4.isVisible = false

                    binding.groupRvEmpty.isVisible = data.isEmpty()
                }
            }

            override fun onFailure(call: Call<Pesanan>, t: Throwable) {
                binding.progressBar4.isVisible = false
                Toast.makeText(this@PesananActivity, "" + t, Toast.LENGTH_SHORT).show()
            }
        })

    }


    override fun onItemClick(item: DataItem?, jenis: String) {
        val bundle = Bundle()
        if (jenis == "bayar") {
            bundle.putString("id_user", item!!.userId.toString())
            bundle.putString("id_kursi_pesanan", item.id_kursi_pesanan)
            bundle.putString("id_jadwal", item.jadwalId.toString())
            bundle.putString("kode_pesanan", item.kodePesanan.toString())


            val intent = Intent(this@PesananActivity, PesananUploadPembayaran::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
        if (jenis == "review") {
            bundle.putString("kode_pesanan", item!!.kodePesanan.toString())
            bundle.putString("id_mobil", item.mobilId.toString())
            val intent = Intent(this@PesananActivity, KirimReview::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        pesananRequest();
    }

    override fun onResume() {
        super.onResume()
        pesananRequest()
    }


}