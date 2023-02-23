package com.app.travel.activity

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.databinding.ActivityUbahPasswordBinding
import com.app.travel.network.BaseResponseApi
import com.app.travel.network.RetrofitService
import com.app.travel.utils.DialogLoading
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UbahPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUbahPasswordBinding
    val dialog: DialogLoading = DialogLoading(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.btnUbahPass.setOnClickListener {
            val passwordBaru = binding.edtPasswordBaru.text.toString()
            val passwordKonfirmasi = binding.edtPasswordKonfirmasi.text.toString()

            if (passwordBaru != passwordKonfirmasi) {
                Toast.makeText(
                    this,
                    "Password baru dan passowrd konfirmasi harus sama",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                updatePasswordRequest()
            }
        }
    }

    private fun updatePasswordRequest() {
        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        bodyBuilder.addFormDataPart("password_lama", binding.edtPasswordBaru.text.toString())
        bodyBuilder.addFormDataPart("password_baru", binding.edtPasswordKonfirmasi.text.toString())


        dialog.startLoadingdialog();

        val requestBody = bodyBuilder.build()
        RetrofitService.create(this).updatePassword(requestBody)
            .enqueue(object : Callback<BaseResponseApi> {
                override fun onResponse(
                    call: Call<BaseResponseApi>, response: Response<BaseResponseApi>
                ) {
                    if (response.body()!!.success!!) {
                        dialog.dismissdialog();
                        MaterialAlertDialogBuilder(this@UbahPasswordActivity).setTitle("Password Berhasl diubah, silahkan logout dan login dengan password yang baru")
                            .setCancelable(false)
                            .setNegativeButton("Ok") { dialog, _ ->
                                dialog.dismiss()
                                val intent = Intent(
                                    this@UbahPasswordActivity,
                                    MainActivity::class.java
                                )
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }.show()
                    } else {
                        Toast.makeText(
                            this@UbahPasswordActivity, response.body()!!.message, Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismissdialog();
                    }
                }

                override fun onFailure(call: Call<BaseResponseApi>, t: Throwable) {
                    dialog.dismissdialog();
                    Toast.makeText(this@UbahPasswordActivity, "" + t, Toast.LENGTH_SHORT).show()
                }
            })

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