package com.app.travel.activity

import android.Manifest
import android.R
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.webkit.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.travel.databinding.ActivityPesananUploadPembayaranBinding
import com.app.travel.model.pesanan.Pesanan
import com.app.travel.network.BaseResponseApi
import com.app.travel.network.Config
import com.app.travel.network.RetrofitService
import com.app.travel.network.SessionManager
import com.app.travel.utils.DialogLoading
import com.app.travel.utils.GetFilePath
import com.app.travel.utils.MainCameraActivity
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream


class PesananUploadPembayaran : AppCompatActivity() {

    private var WRITE_EXTERNAL_STORAGE_PERMISSION_CODE: Int = 1
    private var READ_EXTERNAL_STORAGE_PERMISSION_CODE: Int = 2
    private var CAMERA_PERMISSION_CODE: Int = 3
    private lateinit var binding: ActivityPesananUploadPembayaranBinding
    private var imageUri: Uri? = null
    private lateinit var ImgBukti: File
    var bundle: Bundle? = null
    val dialog: DialogLoading = DialogLoading(this)
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananUploadPembayaranBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sessionManager = SessionManager(this)

        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        checkPermission()

        bundle = intent.extras
        binding.btnProses.setOnClickListener {

            if (imageUri == null) {
                MaterialAlertDialogBuilder(this@PesananUploadPembayaran).setTitle("Bukti Pembayaran Belum Diupload")
                    .setNegativeButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }.show()
            } else {
                MaterialAlertDialogBuilder(this@PesananUploadPembayaran).setTitle("Proses Pesanan, dan Kirim Ke Admin")
                    .setNegativeButton("Kembali") { dialog, _ ->
                        dialog.dismiss()
                    }.setPositiveButton("Ok, Lanjutkan") { dialog, _ ->
                        uploadBuktiBayarRequest()
                    }.show()
            }
        }

        binding.btnBatalkan.setOnClickListener {
            MaterialAlertDialogBuilder(this@PesananUploadPembayaran).setTitle("Apakah anda yakin ingin membatalkan pesanan ?")
                .setNegativeButton("tidak") { dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton("Ok, Batalkan") { dialog, _ ->
                    batalkanPesananRequest()
                }.show()
        }

        binding.btnUploadBukti.setOnClickListener {
            val takePictOptions = arrayOf("Kamera", "Galeri")
            AlertDialog.Builder(this).setTitle("Ambil gambar melalui")
                .setItems(takePictOptions) { _, which ->
                    when (which) {
                        0 -> openCamera()
                        1 -> openGallery()
                    }
                }.create().show()


        }


        val id_kursi_pesanan = bundle!!.getString("id_kursi_pesanan")
        val id_user = bundle!!.getString("id_user")
        val id_jadwal = bundle!!.getString("id_jadwal")
        val kode_pesanan = bundle!!.getString("kode_pesanan")


        binding.webviewDetailPemesanan.loadUrl(sessionManager.getIPServer() + "/api/pesanan/detail/bayar?kode_pesanan=$kode_pesanan&id_user=$id_user&id_jadwal=$id_jadwal&id_kursi_pesanan=$id_kursi_pesanan")
        binding.webviewDetailPemesanan.webViewClient = WebViewClient()

        binding.webviewDetailPemesanan.settings.javaScriptEnabled = true
        binding.webviewDetailPemesanan.webChromeClient = WebChromeClient()
        binding.webviewDetailPemesanan.addJavascriptInterface(
            PesanKursiActivity.JavaScriptInterface(
                this@PesananUploadPembayaran
            ), "Android"
        )

        binding.webviewDetailPemesanan.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?, request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.webviewDetailPemesanan.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
                )
                binding.webviewDetailPemesanan.loadUrl("javascript:getData('ad')")
                binding.loading.isVisible = false
                binding.refresh.isRefreshing = false
            }

            override fun onRenderProcessGone(
                view: WebView?, detail: RenderProcessGoneDetail?
            ): Boolean {
                return super.onRenderProcessGone(view, detail)
            }
        }
        pesananDetail(bundle!!.getString("kode_pesanan")!!)


        binding.refresh.setOnRefreshListener {
            binding.webviewDetailPemesanan.reload()
            pesananDetail(bundle!!.getString("kode_pesanan")!!)
        }

    }
//    end


    private fun pesananDetail(kode_pesanan: String) {
        RetrofitService.create(this).pesananDetail(kode_pesanan)
            .enqueue(object : Callback<Pesanan> {
                override fun onResponse(call: Call<Pesanan>, response: Response<Pesanan>) {
                    if (response.isSuccessful) {
                        val data = response.body()

                        if (data!!.data!![0]!!.statusPesanan == "DITOLAK") {
                            binding.groupLunas.visibility = View.INVISIBLE
                            binding.btnBatalkan.isVisible = false
                            binding.tvStatusPembayaran.text = "Pesanan Anda Ditolak"
                            binding.tvStatusPembayaranDesc.text = data.data!![0]!!.pesanTolak
                            binding.tvStatusPembayaran.setTextColor(Color.parseColor("#007c00"));
                        }
                        else if (data.data!![0]!!.statusPembayaran == "LUNAS") {
                            binding.groupLunas.visibility = View.INVISIBLE
                            binding.btnBatalkan.isVisible = false
                            binding.tvStatusPembayaran.text = "Pembayaran Terkonfirmasi"
                            binding.tvStatusPembayaranDesc.text =
                                "Pesanan anda telah berhasil dibuat dan dikonfirmasi oleh admin"
                            binding.tvStatusPembayaran.setTextColor(Color.parseColor("#007c00"));
                        } else if (data.data!![0]!!.statusPembayaran == "BELUM" && (data.data[0]!!.buktiPembayaran != null && data.data[0]!!.buktiPembayaran != "")) {
                            binding.groupLunas.visibility = View.INVISIBLE
                            binding.tvStatusPembayaran.text = "Menunggu Konfirmasi Admin"
                            binding.tvStatusPembayaranDesc.text =
                                "Admin sedang memverifikasi bukti Pembayaran"
                        } else if (data.data[0]!!.statusPembayaran == "BELUM" && data.data[0]!!.buktiPembayaran == null) {
                            binding.groupLunas.visibility = View.VISIBLE
                            binding.tvStatusPembayaran.text = "Menunggu Pembayaran"
                            binding.tvStatusPembayaranDesc.text =
                                "Tolong Segera Melakukan Pengunggahan bukti pembayaran untuk menyelesaikan pemesanan"
                        }
                    }
                }

                override fun onFailure(call: Call<Pesanan>, t: Throwable) {
                    Toast.makeText(this@PesananUploadPembayaran, "" + t, Toast.LENGTH_SHORT).show()
                }
            })


    }

    private fun batalkanPesananRequest() {
        dialog.startLoadingdialog()
        RetrofitService.create(this).batalkanPesanan(bundle!!.getString("kode_pesanan"))
            .enqueue(object : Callback<BaseResponseApi> {
                override fun onResponse(
                    call: Call<BaseResponseApi>, response: Response<BaseResponseApi>
                ) {
                    if (response.isSuccessful) {
                        dialog.dismissdialog()
                        val data = response.body()!!
                        if (data.success == true) {
                            MaterialAlertDialogBuilder(this@PesananUploadPembayaran).setTitle("Pesanan Berhasil Dibatalkan")
                                .setCancelable(false)
                                .setPositiveButton("Ok") { dialog, _ ->
                                    val intent = Intent(
                                        this@PesananUploadPembayaran, MainActivity::class.java
                                    )
                                    intent.putExtras(bundle!!)
                                    intent.flags =
                                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }.show()
                        }

                    }
                }

                override fun onFailure(call: Call<BaseResponseApi>, t: Throwable) {
                    dialog.dismissdialog()
                    Toast.makeText(this@PesananUploadPembayaran, "" + t, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun uploadBuktiBayarRequest() {


        val kode_pesanan = bundle!!.getString("kode_pesanan")
        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        bodyBuilder.addFormDataPart("kode_pesanan", kode_pesanan.toString())
        bodyBuilder.addFormDataPart(
            "img_bukti", ImgBukti.name, ImgBukti.asRequestBody("*".toMediaTypeOrNull())
        )
        dialog.startLoadingdialog();

        val requestBody = bodyBuilder.build()
        RetrofitService.create(this).uploadBukti(requestBody)
            .enqueue(object : Callback<BaseResponseApi> {
                override fun onResponse(
                    call: Call<BaseResponseApi>, response: Response<BaseResponseApi>
                ) {
                    if (response.body()!!.success!!) {
                        Toast.makeText(
                            this@PesananUploadPembayaran,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismissdialog();

                        val id_kursi_pesanan = bundle!!.getString("id_kursi_pesanan")
                        val id_user = bundle!!.getString("id_user")
                        val id_jadwal = bundle!!.getString("id_jadwal")


                        bundle?.putString("id_kursi_pesanan", id_kursi_pesanan)
                        bundle?.putString("id_user", id_user)
                        bundle?.putString("id_jadwal", id_jadwal)
                        bundle?.putString("kode_pesanan", bundle!!.getString("kode_pesanan"))
                        val intent = Intent(
                            this@PesananUploadPembayaran, PesananDetail::class.java
                        )
                        intent.putExtras(bundle!!)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)


                    } else {
                        Toast.makeText(
                            this@PesananUploadPembayaran,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismissdialog();
                    }
                }

                override fun onFailure(call: Call<BaseResponseApi>, t: Throwable) {
                    dialog.dismissdialog();
                    Toast.makeText(this@PesananUploadPembayaran, "" + t, Toast.LENGTH_SHORT).show()
                }
            })


    }


    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when (PackageManager.PERMISSION_DENIED) {
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) -> {
                    requestPermissions(
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        WRITE_EXTERNAL_STORAGE_PERMISSION_CODE
                    )
                }
                checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        READ_EXTERNAL_STORAGE_PERMISSION_CODE
                    )
                }
                checkSelfPermission(Manifest.permission.CAMERA) -> {
                    requestPermissions(
                        arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            WRITE_EXTERNAL_STORAGE_PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(
                        this,
                        "Anda perlu memberikan semua izin untuk menggunakan aplikasi ini.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
            READ_EXTERNAL_STORAGE_PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(
                        this,
                        "Anda perlu memberikan semua izin untuk menggunakan aplikasi ini.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
            CAMERA_PERMISSION_CODE -> if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(
                        this,
                        "Anda perlu memberikan semua izin untuk menggunakan aplikasi ini.",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(this, MainCameraActivity::class.java)
        getResult.launch(intent)
    }

    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                val uri = data.extras?.get("data")!! as Uri
                ImgBukti = File(uri.path!!)
                imageUri = uri
                Glide.with(this).load(uri).into(binding.imgBukti)
            }
        }
    }


    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncherGallery.launch(galleryIntent)
    }

    private var resultLauncherGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data!!.data!!
                val thumb = if (Build.VERSION.SDK_INT >= 29) {

                    val inputStream: InputStream = contentResolver.openInputStream(imageUri!!)!!
                    BitmapFactory.decodeStream(inputStream)
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                }
                val path: String = GetFilePath.createCopyAndReturnRealPath(this, data.data)!!
                ImgBukti = File(path)

                thumb.let {
                    Glide.with(this).load(it).into(binding.imgBukti)
                }
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


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (bundle != null) {
            if (bundle!!.getBoolean("konfirmasi")) {

                val intent = Intent(
                    this@PesananUploadPembayaran, MainActivity::class.java
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)

            } else {
                super.onBackPressed()
            }
        }
    }
}