package com.app.travel.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.travel.adapter.ReviewPenggunaAdapter
import com.app.travel.databinding.ActivityReviewBinding
import com.app.travel.model.DataItemReview
import com.app.travel.model.ReviewPengguna
import com.app.travel.network.RetrofitService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReviewActivity : AppCompatActivity(), ReviewPenggunaAdapter.OnItemClickListener {
    private lateinit var binding: ActivityReviewBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        val bundle = intent.extras
        reviewPenggunaRequest(bundle!!.getString("id_mobil")!!)

        binding.refresh.setOnRefreshListener {
            reviewPenggunaRequest(bundle.getString("id_mobil")!!)
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

    private fun reviewPenggunaRequest(id_mobil: String) {
        binding.rvReviewPengguna.layoutManager = LinearLayoutManager(this)
        binding.rvReviewPengguna.adapter = null
        RetrofitService.create(this).reviewByMobil(id_mobil)
            .enqueue(object : Callback<ReviewPengguna> {
                override fun onResponse(
                    call: Call<ReviewPengguna>,
                    response: Response<ReviewPengguna>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()!!.data
                        val adapter =
                            ReviewPenggunaAdapter(data, this@ReviewActivity, this@ReviewActivity)
                        binding.rvReviewPengguna.adapter = adapter
                        binding.refresh.isRefreshing = false
                     if(response.body()!!.data?.isEmpty()!!){
                         binding.imageView6.visibility = View.VISIBLE
                         binding.textView11.visibility = View.VISIBLE
                     }else{
                         binding.imageView6.visibility = View.GONE
                         binding.textView11.visibility = View.GONE
                     }

                    }
                }

                override fun onFailure(call: Call<ReviewPengguna>, t: Throwable) {
                    Toast.makeText(this@ReviewActivity, "" + t, Toast.LENGTH_SHORT).show()
                }
            })

    }


    override fun onItemClickedLayananSyarat(dataItemReview: DataItemReview?, s: String) {

    }


}