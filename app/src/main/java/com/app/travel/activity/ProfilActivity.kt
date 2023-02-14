package com.app.travel.activity

//noinspection SuspiciousImport
import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.app.travel.databinding.ActivityProfilBinding
import com.app.travel.model.UserDetail
import com.app.travel.network.RetrofitService
import com.app.travel.network.SessionManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfilBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        sessionManager = SessionManager(this)
        setSupportActionBar(binding.topAppBar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);

        binding.btnLogout.setOnClickListener{
            MaterialAlertDialogBuilder(this)
                .setTitle("Apakah anda ingin keluar dari akun ?")
                .setNegativeButton("batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .setPositiveButton("Ya, Keluar") { _, _ ->
                    sessionManager.deleteAuthToken()
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                .show()
        }

        binding.btnUbahPass.setOnClickListener{
            startActivity(Intent(this, UbahPasswordActivity::class.java))
        }

        binding.btnUpdateProfil.setOnClickListener{
            updateProfilRequest()
        }

        binding.btnUploadFoto.setOnClickListener{

        }



        userDetailRequest()

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateProfilRequest() {

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
                }
            }
            override fun onFailure(call: Call<UserDetail>, t: Throwable) {
                Toast.makeText(this@ProfilActivity, ""+ t, Toast.LENGTH_SHORT).show()
            }
        })

    }

}