package com.app.travel.activity

//noinspection SuspiciousImport

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.R
import com.app.travel.databinding.ActivityProfilBinding
import com.app.travel.model.UserDetail
import com.app.travel.network.BaseResponseApi
import com.app.travel.network.RetrofitService
import com.app.travel.network.SessionManager
import com.app.travel.utils.DialogLoading
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfilBinding
    private lateinit var sessionManager: SessionManager
    val dialog: DialogLoading = DialogLoading(this)
    private lateinit var jenkel: String

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
                    sessionManager.deleteAuthToken()
                    startActivity(Intent(this, LoginActivity::class.java))
                }.show()
        }

        binding.btnUbahPass.setOnClickListener {
            startActivity(Intent(this, UbahPasswordActivity::class.java))
        }

        binding.btnUpdateProfil.setOnClickListener {
            updateProfilRequest()
        }

        binding.btnUploadFoto.setOnClickListener {

        }

        binding.radioGroupJenkel.setOnCheckedChangeListener { group, checkedId ->
            jenkel = if (R.id.radio_laki == checkedId){
                "L"
            }else{
                "P"
            }

        }



        userDetailRequest()

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            androidx.appcompat.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun userDetailRequest() {
        RetrofitService.create(this).userDetail().enqueue(object : Callback<UserDetail> {
            override fun onResponse(call: Call<UserDetail>, response: Response<UserDetail>) {
                if (response.isSuccessful) {
                    val data = response.body()!!.data!!
                    binding.edtEmail1.setText(data.email.toString())
                    binding.edtKontak.setText(data.kontak.toString())
                    binding.edtNamaLengkap.setText(data.namaLengkap.toString())
                    binding.edtAlamat.setText(data.alamat.toString())

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

    private fun updateProfilRequest() {
        dialog.startLoadingdialog()
        RetrofitService.create(this).updateProfil(
            binding.edtNamaLengkap.text.toString(),
            binding.edtEmail1.text.toString(),
            binding.edtKontak.text.toString(),
            binding.edtAlamat.text.toString(),
            jenkel
        )
            .enqueue(object : Callback<BaseResponseApi> {
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