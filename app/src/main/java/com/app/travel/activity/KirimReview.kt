package com.app.travel.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.databinding.ActivityKirimReviewBinding
import com.app.travel.network.BaseResponseApi
import com.app.travel.network.RetrofitService
import com.app.travel.utils.DialogLoading
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KirimReview : AppCompatActivity() {
    var bundle: Bundle? = null
    val dialog : DialogLoading = DialogLoading(this)

    private lateinit var binding: ActivityKirimReviewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKirimReviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        binding.btnKirimReview.setOnClickListener {
            if(binding.bintangReview2.rating.toString() == "" || binding.bintangReview2.rating.toString() == "0"){
                MaterialAlertDialogBuilder(this@KirimReview)
                    .setTitle("Review Bintang belum diisi")
                    .setNegativeButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }

                    .show()
            }
            else if(binding.edtReview.text.isEmpty()){
                MaterialAlertDialogBuilder(this@KirimReview)
                    .setTitle("Pesan Review belum diisi")
                    .setNegativeButton("Ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }else{
                kirimReviewRequest()
            }

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

    private fun kirimReviewRequest() {
        bundle = intent.extras
        val kode_pesanan = bundle!!.getString("kode_pesanan")



        val bodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        bodyBuilder.addFormDataPart("kode_pesanan", kode_pesanan.toString())
        bodyBuilder.addFormDataPart("rating_nilai", binding.bintangReview2.rating.toString())
        bodyBuilder.addFormDataPart("rating_komen", binding.edtReview.text.toString())

        dialog.startLoadingdialog();

        val requestBody = bodyBuilder.build()
        RetrofitService.create(this).kirimReview(requestBody)
            .enqueue(object : Callback<BaseResponseApi> {
                override fun onResponse(
                    call: Call<BaseResponseApi>,
                    response: Response<BaseResponseApi>
                ) {
                    if (response.body()!!.success!!) {
                        dialog.dismissdialog();
                        MaterialAlertDialogBuilder(this@KirimReview)
                            .setTitle("Berhasil Mengirim Review")
                            .setPositiveButton("Ok") { dialog, _ ->
                               onBackPressed()
                            }
                            .show()
                    } else {
                        Toast.makeText(
                            this@KirimReview,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismissdialog();
                    }
                }

                override fun onFailure(call: Call<BaseResponseApi>, t: Throwable) {
                    dialog.dismissdialog();
                    Toast.makeText(this@KirimReview, "" + t, Toast.LENGTH_SHORT).show()
                }
            })

    }
}