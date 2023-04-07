package com.app.travel.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.travel.R
import com.app.travel.databinding.ActivityJadwalTourDetailBinding
import com.app.travel.model.Jadwal
import com.app.travel.network.RetrofitService
import com.app.travel.network.SessionManager
import com.app.travel.utils.convertToRupiah
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JadwalTourDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJadwalTourDetailBinding
    private lateinit var sessionManager: SessionManager
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJadwalTourDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val bundle = intent.extras
        sessionManager = SessionManager(this)
        jadwalRequest(bundle!!.getString("id_jadwal")!!)


        binding.webviewGaleri.loadUrl( sessionManager.getIPServer()+ "/api/tour/galeri?id_jadwal="+ bundle.getString("id_jadwal")!!)
        binding.webviewGaleri.webViewClient = WebViewClient()
        binding.webviewGaleri.settings.javaScriptEnabled = true
        binding.webviewGaleri.webChromeClient = WebChromeClient()
        binding.webviewGaleri.addJavascriptInterface(
            PesanKursiActivity.JavaScriptInterface(
                this@JadwalTourDetailActivity
            ), "Android"
        );


        binding.webviewGaleri.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.webviewGaleri.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                binding.webviewGaleri.loadUrl("javascript:getData('ad')");
            }

            override fun onRenderProcessGone(
                view: WebView?,
                detail: RenderProcessGoneDetail?
            ): Boolean {
                return super.onRenderProcessGone(view, detail)
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun jadwalRequest(id_jadwal: String) {
        RetrofitService.create(this).jadwalDetail(id_jadwal).enqueue(object :
            Callback<Jadwal> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<Jadwal>, response: Response<Jadwal>) {
                if (response.isSuccessful) {
                    val item = response.body()!!.data?.get(0)
                    val sessionManager = SessionManager(this@JadwalTourDetailActivity)
                    Glide
                        .with(this@JadwalTourDetailActivity)
                        .load(sessionManager.getIPServer()+"/storage/" + item!!.tourBrosur!!)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .into(binding.imgFotoUser);

                    binding.tvTourJudul.text = item.tourJudul
                    binding.tvHarga.text = item.harga?.toString()!!.convertToRupiah()
                    binding.tvNamaMobil.text = item.mobil!!.nama.toString()
                    binding.tvJenisMobil.text = item.mobil.jenis.toString()
                    binding.tvWaktu.text = item.tanggal + " (" + item.jam + " WIB)"
                    binding.tvKursi.text = item.kursiTersedia.toString()
                    binding.tvStatus.text = item.status.toString()
                    binding.tvTourDeskripsi.text = item.tourDesc.toString()

                    binding.btnPesan.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("id_jadwal", item.id.toString())
                        Log.i("id_jadwal", "onItemClickedLayananSyarat:"+ item.id.toString())
                        val intent = Intent(this@JadwalTourDetailActivity, PesanKursiActivity::class.java)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<Jadwal>, t: Throwable) {


            }
        })

    }
}