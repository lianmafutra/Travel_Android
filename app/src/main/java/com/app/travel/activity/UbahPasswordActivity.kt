package com.app.travel.activity

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.app.travel.databinding.ActivityUbahPasswordBinding

class UbahPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUbahPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUbahPasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.topAppBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.btnUbahPass.setOnClickListener{
            val passwordBaru = binding.edtPasswordBaru.text.toString()
            val passwordKonfirmasi = binding.edtPasswordKonfirmasi.text.toString()

            if(passwordBaru != passwordKonfirmasi){
                Toast.makeText(this, "Password baru dan passowrd konfirmasi harus sama", Toast.LENGTH_SHORT).show()
            }else{
                updatePasswordRequest(passwordBaru)
            }
        }
    }

    private fun updatePasswordRequest(password_baru: String){

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