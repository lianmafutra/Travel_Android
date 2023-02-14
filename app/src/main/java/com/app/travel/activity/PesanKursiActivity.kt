package com.app.travel.activity

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.databinding.ActivityPesanKursiBinding
import com.app.travel.network.Config
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

        // onPageFinished and override Url loading.
        binding.webviewKursi.webViewClient = WebViewClient()

        // this will load the url of the website
        binding.webviewKursi.loadUrl(Config.BASE_URL+"/api/jadwal/kursi/13")

        // this will enable the javascript settings, it can also allow xss vulnerabilities
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

        binding.btnLanjutPesanKursi.setOnClickListener {
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

