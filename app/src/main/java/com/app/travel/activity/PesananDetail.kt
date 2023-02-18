package com.app.travel.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.webkit.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.travel.databinding.ActivityPesananDetailBinding
import com.app.travel.network.Config

class PesananDetail : AppCompatActivity() {
    private lateinit var binding: ActivityPesananDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        setSupportActionBar(binding.topAppBar)
//        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.btnKembali.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val bundle = intent.extras
        val id_kursi_pesanan = bundle!!.getString("id_kursi_pesanan")
        val id_user = bundle.getString("id_user")
        val id_jadwal = bundle.getString("id_jadwal")

        binding.swiperefresh.setOnRefreshListener {
            binding.webviewDetailPemesanan.reload()
        }

        binding.webviewDetailPemesanan.loadUrl(Config.BASE_URL + "/api/pesanan/detail/verifikasi?id_user=$id_user&id_jadwal=$id_jadwal&id_kursi_pesanan=$id_kursi_pesanan")
        binding.webviewDetailPemesanan.webViewClient = WebViewClient()


        binding.webviewDetailPemesanan.settings.javaScriptEnabled = true
        binding.webviewDetailPemesanan.webChromeClient = WebChromeClient()
        binding.webviewDetailPemesanan.addJavascriptInterface(
            PesanKursiActivity.JavaScriptInterface(
                this@PesananDetail
            ), "Android"
        );


        binding.webviewDetailPemesanan.webViewClient = object : WebViewClient() {
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
                binding.webviewDetailPemesanan.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                binding.webviewDetailPemesanan.loadUrl("javascript:getData('ad')");
                binding.loading.isVisible = false
                binding.swiperefresh.isRefreshing = false

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
}