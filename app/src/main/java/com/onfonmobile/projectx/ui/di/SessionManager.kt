package com.onfonmobile.projectx.ui.di

import android.content.Context
import android.content.SharedPreferences
import java.util.Date

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREF_NAME,
        Context.MODE_PRIVATE
    )
    private val editor = sharedPreferences.edit()

    companion object {
        private const val PREF_NAME = "UserSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ID = "userId"
        private const val KEY_LOGIN_TIME = "loginTime"
        private const val SESSION_TIMEOUT = 5 * 60 * 1000
       // private const val SESSION_TIMEOUT = 24 * 60 * 60 * 1000 // 24 hours in milliseconds
    }

    fun createSession(username: String, userId: Long) {
        editor.apply {
//            putBoolean(KEY_IS_LOGGED_IN, true)
//            putString(KEY_USERNAME, username)
//            putLong(KEY_USER_ID, userId)
            putLong(KEY_LOGIN_TIME, Date().time)
            editor.putBoolean("isLoggedIn", true)
            editor.putString("username", username) // ✅ Store username
            editor.putLong("userId", userId)
            editor.apply()
            apply()
        }
    }

//    fun isLoggedIn(): Boolean {
//        if (!sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
//            return false
//        }
//
//        // Check session timeout
//        val loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0)
//        val currentTime = Date().time
//        return currentTime - loginTime < SESSION_TIMEOUT
//    }
fun isLoggedIn(): Boolean {
    if (!sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
        return false
    }


    // Check session timeout
    val loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0)
    val currentTime = Date().time
    if (currentTime - loginTime >= SESSION_TIMEOUT) {
        logout() // Automatically logout after timeout
        return false
    }
    return true
}

    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    fun getUserId(): Int {
        return sharedPreferences.getInt(KEY_USER_ID, -1)
    }

    fun logout() {
        editor.apply {
            clear()
            apply()
        }
    }
}