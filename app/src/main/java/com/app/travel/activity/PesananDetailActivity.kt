package com.app.travel.activity

import android.R
import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.webkit.*
import androidx.core.view.isVisible
import com.app.travel.databinding.ActivityPesananBinding
import com.app.travel.databinding.ActivityPesananDetailBinding
import com.app.travel.network.Config

class PesananDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPesananDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);


        binding.webviewDetailPemesanan.loadUrl("")
        binding.webviewDetailPemesanan.webViewClient = WebViewClient()
        binding.webviewDetailPemesanan.settings.javaScriptEnabled = true
        binding.webviewDetailPemesanan.webChromeClient = WebChromeClient()
        binding.webviewDetailPemesanan.addJavascriptInterface(PesanKursiActivity.JavaScriptInterface(this@PesananDetailActivity), "Android");

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
                binding.loading.isVisible = false
            }
            override fun onRenderProcessGone(
                view: WebView?,
                detail: RenderProcessGoneDetail?
            ): Boolean {
                return super.onRenderProcessGone(view, detail)
            }
        }
    }

    class JavaScriptInterface internal constructor(c: Context) {
        private var mContext: Context
        var data: String? = null
        init {
            mContext = c
        }

        @JavascriptInterface
        fun sendData(id: String?) {
            this.data=id;

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
}