package com.example.farmhand.navigation

import android.content.Context
import android.content.SharedPreferences

object AuthManager {
    private const val PREF_NAME = "auth_prefs"
    private const val KEY_IS_LOGGED_IN = "isLoggedIn"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveAuthState(context: Context, isLoggedIn: Boolean) {
        val prefs = getPreferences(context)
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isUserLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout(context: Context) {
        saveAuthState(context, false)
    }
}