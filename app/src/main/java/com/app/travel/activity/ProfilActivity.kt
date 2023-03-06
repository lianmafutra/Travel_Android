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
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.R
import com.app.travel.databinding.ActivityProfilBinding
import com.app.travel.model.UserDetail
import com.app.travel.network.BaseResponseApi
import com.app.travel.network.RetrofitService
import com.app.travel.network.SessionManager
import com.app.travel.utils.DialogLoading
import com.app.travel.utils.GetFilePath
import com.app.travel.utils.MainCameraActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.InputStream


class ProfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfilBinding
    private lateinit var sessionManager: SessionManager
    val dialog: DialogLoading = DialogLoading(this)
    private lateinit var jenkel: String
    private var imageUri: Uri? = null
    private lateinit var ImgFoto: File
    private var WRITE_EXTERNAL_STORAGE_PERMISSION_CODE: Int = 1
    private var READ_EXTERNAL_STORAGE_PERMISSION_CODE: Int = 2
    private var CAMERA_PERMISSION_CODE: Int = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sessionManager = SessionManager(this)

        setSupportActionBar(binding.topAppBar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        binding.btnLogout.setOnClickListener {
            MaterialAlertDialogBuilder(this).setTitle("Apakah anda ingin keluar dari akun ?")
                .setNegativeButton("batal") { dialog, _ ->
                    dialog.dismiss()
                }.setPositiveButton("Ya, Keluar") { _, _ ->
                    logoutRequest()
                }.show()
        }

        binding.btnUbahPass.setOnClickListener {
            startActivity(Intent(this, UbahPasswordActivity::class.java))
        }

        binding.btnUpdateProfil.setOnClickListener {
            updateProfilRequest()
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
            jenkel = if (R.id.radio_laki == checkedId) {
                "L"
            } else {
                "P"
            }

        }

        checkPermission()
        userDetailRequest()

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


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
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
                updateFotoRequest()
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
                updateFotoRequest()
            }
        }


    private fun userDetailRequest() {
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loader_circle)
            .error(R.drawable.ic_user)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform()

        RetrofitService.create(this).userDetail().enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                if (response.isSuccessful) {
                    val data = response.body()!!.data!!
                    binding.edtEmail1.setText(data.email.toString())
                    binding.edtKontak.setText(data.kontak.toString())
                    binding.edtNamaLengkap.setText(data.namaLengkap.toString())
                    binding.edtAlamat.setText(data.alamat.toString())

                    Glide.with(this@ProfilActivity).load(sessionManager.getIPServer()+"/storage/"+data.foto).apply(options).into(binding.imgFoto)

                    if (data.jenisKelamin.toString() == "L") {
                        binding.radioGroupJenkel.check(binding.radioLaki.id)
                    } else {
                        binding.radioGroupJenkel.check(binding.radioPerempuan.id)
                    }
                }
            }

            override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                Toast.makeText(this@ProfilActivity, "" + t, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun updateFotoRequest() {

        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        bodyBuilder.addFormDataPart(
            "img_foto", ImgFoto.name, ImgFoto.asRequestBody("*".toMediaTypeOrNull())
        )
        dialog.startLoadingdialog();

        val requestBody = bodyBuilder.build()
        RetrofitService.create(this).updateFoto(requestBody)
            .enqueue(object : Callback<BaseResponseApi> {
                override fun onResponse(
                    call: Call<BaseResponseApi>, response: Response<BaseResponseApi>
                ) {
                    if (response.body()!!.success!!) {
                        Toast.makeText(
                            this@ProfilActivity, response.body()!!.message, Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismissdialog();
                        userDetailRequest()

                    } else {
                        Toast.makeText(
                            this@ProfilActivity, response.body()!!.message, Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismissdialog();
                    }
                }

                override fun onFailure(call: Call<BaseResponseApi>, t: Throwable) {
                    dialog.dismissdialog();
                    Toast.makeText(this@ProfilActivity, "" + t, Toast.LENGTH_SHORT).show()
                }
            })

    }

    private fun logoutRequest() {
        dialog.startLoadingdialog()
        RetrofitService.create(this).logout(sessionManager.getFCMToken()).enqueue(object : Callback<BaseResponseApi> {
            override fun onResponse(
                call: Call<BaseResponseApi>, response: Response<BaseResponseApi>
            ) {
                val data = response.body()
                if (response.isSuccessful) {
                    sessionManager.deleteAuthToken()
                    sessionManager.deleteFCMToken()
                    dialog.dismissdialog()
                    startActivity(Intent(this@ProfilActivity, LoginActivity::class.java))
                } else {
                    dialog.dismissdialog()
                    Toast.makeText(this@ProfilActivity, data.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<BaseResponseApi>, t: Throwable) {
                dialog.dismissdialog()
                Toast.makeText(this@ProfilActivity, "" + t, Toast.LENGTH_SHORT).show()
            }
        })

    }


    private fun updateProfilRequest() {
        dialog.startLoadingdialog()
        RetrofitService.create(this).updateProfil(
            binding.edtNamaLengkap.text.toString(),
            binding.edtEmail1.text.toString(),
            binding.edtKontak.text.toString(),
            binding.edtAlamat.text.toString(),
            jenkel
        ).enqueue(object : Callback<BaseResponseApi> {
                override fun onResponse(
                    call: Call<BaseResponseApi>, response: Response<BaseResponseApi>
                ) {
                    val data = response.body()
                    if (response.isSuccessful) {
                        dialog.dismissdialog()
                        MaterialAlertDialogBuilder(this@ProfilActivity).setTitle(data?.message.toString())
                            .setNegativeButton("Ok") { dialog, _ ->
                                dialog.dismiss()
                                userDetailRequest()
                            }.show()


                    } else {
                        dialog.dismissdialog()
                        Toast.makeText(this@ProfilActivity, data.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<BaseResponseApi>, t: Throwable) {
                    dialog.dismissdialog()
                    Toast.makeText(this@ProfilActivity, "" + t, Toast.LENGTH_SHORT).show()
                }
            })

    }



}