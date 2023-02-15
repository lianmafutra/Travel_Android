package com.app.travel.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.travel.databinding.ActivityPesanKursiBinding
import com.app.travel.network.Config
import com.app.travel.network.Config.URL_PESAN_KURSI
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PesanKursiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPesanKursiBinding

    object Apple {
        var KURSI_PESANAN: String = ""
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesanKursiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.webviewKursi.webViewClient = WebViewClient()


        val bundle = intent.extras
        binding.webviewKursi.loadUrl(URL_PESAN_KURSI+bundle!!.getString("id_jadwal")!!)

        binding.webviewKursi.settings.javaScriptEnabled = true
        binding.webviewKursi.webChromeClient = WebChromeClient()

        // if you want to enable zoom feature
        binding.webviewKursi.settings.setSupportZoom(true)
        binding.webviewKursi.addJavascriptInterface(JavaScriptInterface(this@PesanKursiActivity), "Android");
        binding.webviewKursi.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                MaterialAlertDialogBuilder(this@PesanKursiActivity)
                    .setTitle("Kursi Telah Terisi")
                    .setNegativeButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
//                return super.onJsAlert(view, url, message, result)


                result!!.cancel();
                return true
            }
        }

        binding.webviewKursi.webViewClient = object : WebViewClient() {
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
                binding.loadingWebview.isVisible = false
            }

            override fun onReceivedHttpError(
                view: WebView?,
                request: WebResourceRequest?,
                errorResponse: WebResourceResponse?
            ) {
                Toast.makeText(this@PesanKursiActivity, "Terjadi Kesalahan memuat data", Toast.LENGTH_SHORT).show()
                super.onReceivedHttpError(view, request, errorResponse)

            }

            override fun onRenderProcessGone(
                view: WebView?,
                detail: RenderProcessGoneDetail?
            ): Boolean {
                return super.onRenderProcessGone(view, detail)
            }
        }

        binding.btnLanjutPesanKursi.setOnClickListener {
            bundle.putString("id_kursi_dipesan", Apple.KURSI_PESANAN)
            val intent = Intent(this, PesananDetailActivity::class.java)
            intent.putExtras(bundle)
            startActivity(intent)
            Toast.makeText(this@PesanKursiActivity, Apple.KURSI_PESANAN, Toast.LENGTH_SHORT).show()
        }


    }

    class JavaScriptInterface internal constructor(c: Context) {
        private var mContext: Context
        var data: String? = null
            private var kursi = PesanKursiActivity()
        init {
            mContext = c
        }

        @JavascriptInterface
        fun sendData(kursi_dipilih: String?) {
            this.data=kursi_dipilih;
            Apple.KURSI_PESANAN = data.toString()

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

