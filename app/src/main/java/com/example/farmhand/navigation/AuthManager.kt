package com.example.farmhand.navigation

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object AuthManager {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    private const val KEY_USER_ID = "userID"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuthState(context: Context, isLoggedIn: Boolean, userID: Int) {
        val prefs = getPreferences(context)
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
        prefs.edit().putInt(KEY_USER_ID, userID).apply()
        Log.d("Authmanager", "SharedModel Value: $userID")
    }

    fun isUserLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserIdLoggedIn(context: Context): Int {
        return getPreferences(context).getInt(KEY_USER_ID, -1)
    }

    fun logout(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().clear().apply()
    }
}