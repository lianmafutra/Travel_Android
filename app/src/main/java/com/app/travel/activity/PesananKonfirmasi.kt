package com.app.travel.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.webkit.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.travel.databinding.ActivityPesananKonfirmasiBinding
import com.app.travel.network.BaseResponseApi
import com.app.travel.network.Config
import com.app.travel.network.RetrofitService
import com.app.travel.utils.DialogLoading
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PesananKonfirmasi : AppCompatActivity() {
    var bundle: Bundle? = null
    private lateinit var binding: ActivityPesananKonfirmasiBinding
    val dialog : DialogLoading = DialogLoading(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananKonfirmasiBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        binding.swiperefresh.setOnRefreshListener {
            binding.webviewKonfirmasi.reload()
        }


        binding.btnBuatPesanan.setOnClickListener {
            MaterialAlertDialogBuilder(this@PesananKonfirmasi)
                .setTitle("Konfirmasi Pesanan")
                .setMessage("Apakah Anda Yakin Ingin Membuat Pesanan ?")
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Ok, Lanjutkan") { dialog, _ ->
                    buatPesananRequest()
                }
                .show()

        }

        bundle = intent.extras
        val id_kursi_pesanan = bundle!!.getString("id_kursi_pesanan")
        val id_user = bundle!!.getString("id_user")
        val id_jadwal = bundle!!.getString("id_jadwal")

        binding.webviewKonfirmasi.loadUrl(Config.BASE_URL + "/api/pesanan/detail/konfirmasi?id_user=$id_user&id_jadwal=$id_jadwal&id_kursi_pesanan=$id_kursi_pesanan")
        binding.webviewKonfirmasi.webViewClient = WebViewClient()

        binding.webviewKonfirmasi.settings.cacheMode = WebSettings.LOAD_NO_CACHE;
        binding.webviewKonfirmasi.webChromeClient = WebChromeClient()
        binding.webviewKonfirmasi.addJavascriptInterface(
            PesanKursiActivity.JavaScriptInterface(
                this@PesananKonfirmasi
            ), "Android"
        )

        binding.webviewKonfirmasi.webViewClient = object : WebViewClient() {
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
                binding.webviewKonfirmasi.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                binding.webviewKonfirmasi.loadUrl("javascript:getData('ad')")
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


    private fun buatPesananRequest() {
        bundle = intent.extras
        val id_kursi_pesanan = bundle!!.getString("id_kursi_pesanan")
        val id_user = bundle!!.getString("id_user")
        val id_jadwal = bundle!!.getString("id_jadwal")
        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        bodyBuilder.addFormDataPart("jadwal_id", id_jadwal.toString())
        bodyBuilder.addFormDataPart("id_kursi_pesanan", id_kursi_pesanan.toString())

        dialog.startLoadingdialog();

        val requestBody = bodyBuilder.build()
        RetrofitService.create(this).buatPesanan(requestBody)
            .enqueue(object : Callback<BaseResponseApi> {
                override fun onResponse(
                    call: Call<BaseResponseApi>,
                    response: Response<BaseResponseApi>
                ) {
                    if (response.body()!!.success!!) {

                        dialog.dismissdialog();
                        bundle?.putBoolean("konfirmasi", true)
                        bundle?.putString("id_kursi_pesanan", id_kursi_pesanan)
                        bundle?.putString("id_user", id_user)
                        bundle?.putString("id_jadwal", id_jadwal)
                        bundle?.putString("kode_pesanan",   response.body()!!.message)
                        val intent = Intent(this@PesananKonfirmasi, PesananUploadPembayaran::class.java)
                        intent.putExtras(bundle!!)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)


                    } else {
                        Toast.makeText(
                            this@PesananKonfirmasi,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismissdialog();
                    }
                }

                override fun onFailure(call: Call<BaseResponseApi>, t: Throwable) {
                    dialog.dismissdialog();
                    Toast.makeText(this@PesananKonfirmasi, "" + t, Toast.LENGTH_SHORT).show()
                }
            })

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