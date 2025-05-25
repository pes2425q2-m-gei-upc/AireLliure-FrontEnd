package com.front_pes

import android.content.Context

object UserPreferences {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_NAME = "name"
    private const val KEY_ABOUT = "about"
    private const val KEY_ESTAT = "estat"
    private const val KEY_PUNTS = "punts"
    private const val KEY_ADMIN = "admin"
    private const val KEY_IMAGE = "image"
    private const val KEY_LOGGED_IN = "logged_in"

    fun saveUser(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_EMAIL, CurrentUser.correu)
            putString(KEY_NAME, CurrentUser.nom)
            putString(KEY_ABOUT, CurrentUser.about)
            putString(KEY_ESTAT, CurrentUser.estat)
            putInt(KEY_PUNTS, CurrentUser.punts)
            putBoolean(KEY_ADMIN, CurrentUser.administrador)
            putString(KEY_IMAGE, CurrentUser.imatge)
            putBoolean(KEY_LOGGED_IN, true)
            apply()
        }
    }

    fun loadUser(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val loggedIn = prefs.getBoolean(KEY_LOGGED_IN, false)
        if (!loggedIn) return false

        CurrentUser.correu = prefs.getString(KEY_EMAIL, "") ?: ""
        CurrentUser.nom = prefs.getString(KEY_NAME, "") ?: ""
        CurrentUser.about = prefs.getString(KEY_ABOUT, "") ?: ""
        CurrentUser.estat = prefs.getString(KEY_ESTAT, "") ?: ""
        CurrentUser.punts = prefs.getInt(KEY_PUNTS, 0)
        CurrentUser.administrador = prefs.getBoolean(KEY_ADMIN, false)
        CurrentUser.imatge = prefs.getString(KEY_IMAGE, "") ?: ""
        return true
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().clear().apply()
    }
}
