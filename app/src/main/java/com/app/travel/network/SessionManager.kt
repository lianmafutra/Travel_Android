package com.app.travel.network

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.app.travel.R
import com.app.travel.activity.LoginActivity
import com.app.travel.activity.MainActivity
import com.app.travel.model.User
import com.google.gson.Gson


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

    fun saveUser(user: User?) {
        val editor = prefs.edit()
        val gson = Gson()
        val json = gson.toJson(user)
        editor.putString("user", json)
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

    fun getDataUser(): User? {
        val gson = Gson()
        return gson.fromJson( prefs.getString("user", null), User::class.java);
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