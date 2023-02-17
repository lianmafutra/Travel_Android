package com.app.travel.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.webkit.*
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.app.travel.databinding.ActivityPesananUploadPembayaranBinding
import com.app.travel.network.BaseResponseApi
import com.app.travel.network.Config
import com.app.travel.network.RetrofitService
import com.app.travel.utils.GetFilePath
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
    private var imageUri : Uri? = null;
    private lateinit var ImgBukti: File
    var bundle : Bundle? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananUploadPembayaranBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        checkPermission()


        binding.btnKembali.setOnClickListener {
            onBackPressed()

        }

        binding.btnProses.setOnClickListener {

            if ( imageUri  == null){
                MaterialAlertDialogBuilder(this@PesananUploadPembayaran)
                    .setTitle("Bukti Pembayaran Belum Diupload")
                    .setNegativeButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }else{


                MaterialAlertDialogBuilder(this@PesananUploadPembayaran)
                    .setTitle("Proses Pesanan, dan Kirim Ke Admin")
                    .setNegativeButton("Kembali") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Ok, Lanjutkan") { dialog, _ ->
                        buatPesananRequest()
                    }
                    .show()
            }



        }

        binding.btnUploadBukti.setOnClickListener {
            val takePictOptions = arrayOf("Kamera", "Galeri")
            AlertDialog.Builder(this)
                .setTitle("Ambil gambar melalui")
                .setItems(takePictOptions) { _, which ->
                    when (which) {
                        0 -> openCamera()
                        1 -> openGallery()
                    }
                }
                .create()
                .show()

        }


        bundle = intent.extras
        val id_kursi_pesanan = bundle!!.getString("id_kursi_pesanan")
        val id_user = bundle!!.getString("id_user")
        val id_jadwal = bundle!!.getString("id_jadwal")


        binding.webviewDetailPemesanan.loadUrl(Config.BASE_URL + "/api/pesanan/detail/bayar?id_user=$id_user&id_jadwal=$id_jadwal&id_kursi_pesanan=$id_kursi_pesanan")
        binding.webviewDetailPemesanan.webViewClient = WebViewClient()


        binding.webviewDetailPemesanan.settings.javaScriptEnabled = true
        binding.webviewDetailPemesanan.webChromeClient = WebChromeClient()
        binding.webviewDetailPemesanan.addJavascriptInterface(
            PesanKursiActivity.JavaScriptInterface(
                this@PesananUploadPembayaran
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
        bodyBuilder.addFormDataPart(
            "img_bukti",
            ImgBukti.name,
            ImgBukti.asRequestBody("*".toMediaTypeOrNull())
        )

        val requestBody = bodyBuilder.build()
        RetrofitService.create(this).buatPesanan(requestBody).enqueue(object : Callback<BaseResponseApi> {
            override fun onResponse(call: Call<BaseResponseApi>, response: Response<BaseResponseApi>) {
                if (response.body()!!.success!!) {
                    Toast.makeText(this@PesananUploadPembayaran, response.body()!!.message, Toast.LENGTH_SHORT).show()


                    bundle?.putString("id_kursi_pesanan", id_kursi_pesanan)
                    bundle?.putString("id_user", id_user)
                    bundle?.putString("id_jadwal", id_jadwal)
                    val intent = Intent(this@PesananUploadPembayaran, PesananDetail::class.java)
                    intent.putExtras(bundle!!)
                    startActivity(intent)


                }else{
                    Toast.makeText(this@PesananUploadPembayaran, response.body()!!.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponseApi>, t: Throwable) {
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
                        arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION_CODE
                    )
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
            } else {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        resultLauncherCamera.launch(takePictureIntent)
                    }
                }
            }
        } else {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(packageManager)?.also {
                    resultLauncherCamera.launch(takePictureIntent)
                }
            }
        }
    }

    private var resultLauncherCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data!!.data!!
                val thumb = data.extras?.get("data") as Bitmap
                val path: String = GetFilePath.createCopyAndReturnRealPath(this, data.data)!!
                ImgBukti = File(path)
                thumb.let {
                    Glide.with(this)
                        .load(it)
                        .into(binding.imgBukti)
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
                //the image URI
                imageUri = data!!.data!!
                val thumb = if (Build.VERSION.SDK_INT >= 29) {

                    val inputStream: InputStream = contentResolver.openInputStream(imageUri!!)!!
                    BitmapFactory.decodeStream(inputStream)
                } else {
                    // Use older version
                    MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                }
                val path: String = GetFilePath.createCopyAndReturnRealPath(this, data.data)!!
                ImgBukti = File(path)

                thumb.let {
                    Glide.with(this)
                        .load(it)
                        .into(binding.imgBukti)
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
            this.data = id;

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            androidx.appcompat.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}