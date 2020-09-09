package com.example.instagrammedia

import android.content.Context
import android.content.SharedPreferences

class AppPreferences {

    private var preferences: SharedPreferences? = null

    fun AppPreferences(
        context: Context
    ) {
        preferences = context.getSharedPreferences(Companion.APP_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE)
    }

    fun getString(key: String?): String? {
        return preferences!!.getString(key, null)
    }

    fun putString(key: String?, value: String?) {
        val editor = preferences!!.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun clear() {
        val editor = preferences!!.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        const val APP_PREFERENCES_FILE_NAME = "userdata"
        const val USER_ID = "userID"
        const val TOKEN = "token"
        const val PROFILE_PIC = "profile_pic"
        const val USER_NAME = "username"
    }
}