package com.app.travel.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.*
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.travel.databinding.ActivityPesanKursiBinding

import com.app.travel.network.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class PesanKursiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPesanKursiBinding
    private lateinit var sessionManager: SessionManager

    object DATA {
        var KURSI_PESANAN: String = ""
    }
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesanKursiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        sessionManager = SessionManager(this)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.webviewKursi.webViewClient = WebViewClient()


        val bundle = intent.extras
        binding.webviewKursi.loadUrl(sessionManager.getIPServer()+"/api/jadwal/kursi/"+bundle!!.getString("id_jadwal")!!)

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
                binding.webviewKursi.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            override fun onRenderProcessGone(
                view: WebView?,
                detail: RenderProcessGoneDetail?
            ): Boolean {
                return super.onRenderProcessGone(view, detail)
            }
        }

        binding.btnLanjutPesanKursi.setOnClickListener {



            if(DATA.KURSI_PESANAN.isEmpty()){
                MaterialAlertDialogBuilder(this@PesanKursiActivity)
                    .setTitle("Kursi Belum dipilih")
                    .setNegativeButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()

            }else{
//                bundle.putString("id_kursi_pesanan", DATA.KURSI_PESANAN)
//                bundle.putString("id_user", sessionManager.getDataUser()?.id.toString())
//                bundle.putString("id_jadwal", bundle.getString("id_jadwal")!!)
//                val intent = Intent(this, PesananUploadPembayaran::class.java)
//                intent.putExtras(bundle)
//                DATA.KURSI_PESANAN = ""
//                Log.i("id_jadwal", "pesan kursi lanjut :"+  bundle.getString("id_jadwal")!!)
//                startActivity(intent)


                bundle.putString("id_kursi_pesanan", DATA.KURSI_PESANAN)
                bundle.putString("id_user", sessionManager.getDataUser()?.id.toString())
                bundle.putString("id_jadwal", bundle.getString("id_jadwal")!!)
                val intent = Intent(this, PesananKonfirmasi::class.java)
                intent.putExtras(bundle)
                DATA.KURSI_PESANAN = ""
                Log.i("id_jadwal", "pesan kursi lanjut :"+  bundle.getString("id_jadwal")!!)
                startActivity(intent)
            }
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
            DATA.KURSI_PESANAN = data.toString()

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

    override fun onStart() {
        super.onStart()
        binding.webviewKursi.reload()
    }
}

