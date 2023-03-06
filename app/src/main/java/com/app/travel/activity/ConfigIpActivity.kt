package com.app.travel.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.travel.databinding.ActivityConfigIpBinding
import com.app.travel.network.SessionManager

class ConfigIpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfigIpBinding
    private lateinit var sessionManager: SessionManager
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConfigIpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sessionManager = SessionManager(this)
        binding.tvIp.text = "Server Running IP :"+sessionManager.getIPServer()

        binding.btnLogin2.setOnClickListener {
            sessionManager.saveIPServer("http://"+binding.edtServerIp.text.toString())
            Toast.makeText(this, "Alamat IP Server  Berhasil Diterapkan", Toast.LENGTH_SHORT).show()
            binding.tvIp.text = "Server Running IP :"+sessionManager.getIPServer()
        }


    }
}