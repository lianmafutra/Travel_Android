package com.app.travel.activity

//noinspection SuspiciousImport
import android.Manifest

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.app.travel.R
import com.app.travel.databinding.ActivityRegisterBinding
import com.app.travel.network.BaseResponseApi
import com.app.travel.network.RetrofitService
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


class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    val dialog: DialogLoading = DialogLoading(this)
    private lateinit var jenkel: String
    private var imageUri: Uri? = null
    private lateinit var ImgFoto: File
    private var WRITE_EXTERNAL_STORAGE_PERMISSION_CODE: Int = 1
    private var READ_EXTERNAL_STORAGE_PERMISSION_CODE: Int = 2
    private var CAMERA_PERMISSION_CODE: Int = 3


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.topAppBar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        binding.btnDaftar.setOnClickListener {
            registerRequest()
        }

        binding.btnUploadFoto.setOnClickListener {
            val takePictOptions = arrayOf("Kamera", "Galeri")
            AlertDialog.Builder(this).setTitle("Ambil gambar melalui")
                .setItems(takePictOptions) { _, which ->
                    when (which) {
                        0 -> openCamera()
                        1 -> openGallery()
                    }
                }.create().show()

        }

        binding.radioGroupJenkel.setOnCheckedChangeListener { group, checkedId ->
            jenkel = if (com.app.travel.R.id.radio_laki == checkedId) {
                "L"
            } else {
                "P"
            }

        }




        checkPermission()

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
                ImgFoto = File(uri.path!!)
                Glide.with(this).load(uri).into(binding.imgFoto)
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
                ImgFoto = File(path)
                thumb.let {
                    Glide.with(this).load(it).into(binding.imgFoto)
                }
            }
        }


    private fun registerRequest() {

        if(imageUri == null){
            MaterialAlertDialogBuilder(this@RegisterActivity).setTitle("Foto Profil tidak boleh kosong")
                .setCancelable(false)
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
           return
        }

        if(binding.edtNamaLengkap.text!!.isBlank()){
            MaterialAlertDialogBuilder(this@RegisterActivity).setTitle("Nama lengkap wajib di isi")
                .setCancelable(false)
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
            return
        }

        if(binding.edtEmail1.text!!.isBlank()){
            MaterialAlertDialogBuilder(this@RegisterActivity).setTitle("Email wajib di isi")
                .setCancelable(false)
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
            return
        }
        if(binding.edtKontak.text!!.isBlank()){
            MaterialAlertDialogBuilder(this@RegisterActivity).setTitle("Kontak wajib di isi")
                .setCancelable(false)
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
            return
        }
        if(!binding.radioLaki.isChecked && !binding.radioPerempuan.isChecked){
            MaterialAlertDialogBuilder(this@RegisterActivity).setTitle("Jenis Kelamin wajib di isi")
                .setCancelable(false)
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
            return
        }

        if(binding.edtAlamat.text!!.isBlank()){
            MaterialAlertDialogBuilder(this@RegisterActivity).setTitle("Alamat wajib di isi")
                .setCancelable(false)
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
            return
        }
        if(binding.edtPassword.text!!.isBlank()){
            MaterialAlertDialogBuilder(this@RegisterActivity).setTitle("Password wajib di isi")
                .setCancelable(false)
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
            return
        }
        if(binding.edtPasswordKonfirmasi.text!!.isBlank()){
            MaterialAlertDialogBuilder(this@RegisterActivity).setTitle("Konfirmasi Password wajib di isi")
                .setCancelable(false)
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
            return
        }

        val passwordBaru = binding.edtPassword.text.toString()
        val passwordKonfirmasi = binding.edtPasswordKonfirmasi.text.toString()

        if (passwordBaru != passwordKonfirmasi) {
            MaterialAlertDialogBuilder(this@RegisterActivity).setTitle("Password dan Password konfirmasi harus sama")
                .setCancelable(false)
                .setNegativeButton("Ok") { dialog, _ ->
                    dialog.dismiss()
                }.show()
            return
        }

        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        bodyBuilder.addFormDataPart("nama_lengkap", binding.edtNamaLengkap.text.toString())
        bodyBuilder.addFormDataPart("email", binding.edtEmail1.text.toString())
        bodyBuilder.addFormDataPart("kontak", binding.edtKontak.text.toString())
        bodyBuilder.addFormDataPart("jenis_kelamin", jenkel)
        bodyBuilder.addFormDataPart("alamat", binding.edtAlamat.text.toString())
        bodyBuilder.addFormDataPart("password", binding.edtPassword.text.toString())

        bodyBuilder.addFormDataPart(
            "foto", ImgFoto.name, ImgFoto.asRequestBody("*".toMediaTypeOrNull())
        )
        dialog.startLoadingdialog();

        val requestBody = bodyBuilder.build()
        RetrofitService.create(this).registerUser(requestBody)
            .enqueue(object : Callback<BaseResponseApi> {
                override fun onResponse(
                    call: Call<BaseResponseApi>, response: Response<BaseResponseApi>
                ) {
                    if (response.body()!!.success!!) {
                        dialog.dismissdialog();
                        MaterialAlertDialogBuilder(this@RegisterActivity).setTitle("Selamat Pendaftaran User Berhasil Silahkan Login")
                            .setCancelable(false)
                            .setNegativeButton("Ok, Login") { dialog, _ ->
                                dialog.dismiss()
                                val intent = Intent(this@RegisterActivity,LoginActivity::class.java)
                                    intent.putExtra("email",binding.edtEmail1.text.toString())
                                startActivity(intent)
                            }.show()
                    } else {
                        Toast.makeText(
                            this@RegisterActivity, response.body()!!.message, Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismissdialog();
                    }
                }

                override fun onFailure(call: Call<BaseResponseApi>, t: Throwable) {
                    dialog.dismissdialog();
                    Toast.makeText(this@RegisterActivity, "" + t, Toast.LENGTH_SHORT).show()
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