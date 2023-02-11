package com.app.travel.network

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.app.travel.LoginActivity
import com.app.travel.MainActivity
import com.app.travel.R


class SessionManager(context: Context)  {

    private var prefs: SharedPreferences = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    private var context2 : Context;

    init {
        context2 = context
    }

    companion object {
        const val USER_TOKEN = "user_token"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.apply()
    }

    fun deleteAuthToken() {
        val editor = prefs.edit()
        editor.remove(USER_TOKEN)
        editor.clear()
        editor.apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun authCheck(){
        if (getAuthToken() != null){
            val intent = Intent(context2, MainActivity::class.java)
            context2.startActivity(intent)
        }else{
            val intent = Intent(context2, LoginActivity::class.java)
            context2.startActivity(intent)
        }
    }
}