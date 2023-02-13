package com.app.travel.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.databinding.ActivityReviewBinding
import com.app.travel.model.ReviewPengguna
import com.app.travel.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        val bundle = intent.extras
        reviewPenggunaRequest(bundle!!.getString("id_mobil")!!)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun reviewPenggunaRequest(id_mobil: String) {
        RetrofitService.create(this).reviewByMobil(id_mobil)
            .enqueue(object : Callback<ReviewPengguna> {
                override fun onResponse(
                    call: Call<ReviewPengguna>,
                    response: Response<ReviewPengguna>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()!!.data
                        Toast.makeText(this@ReviewActivity, ""+data.toString(), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<ReviewPengguna>, t: Throwable) {
                    Toast.makeText(this@ReviewActivity, "" + t, Toast.LENGTH_SHORT).show()
                }
            })

    }

}