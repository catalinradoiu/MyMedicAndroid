package com.catalin.mymedic.storage.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import javax.inject.Inject

/**
 * Class for managing the shared preferences
 *
 * @author catalinradoiu
 * @since 4/15/2018
 */
class SharedPreferencesManager @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setCurrentUser(userEmail: String) {
        sharedPreferences.edit().putString(CURRENT_USER, userEmail).apply()
    }

    fun getCurrentUserEmail(): String = sharedPreferences.getString(CURRENT_USER, "")

    fun clearCurrentUser() {
        sharedPreferences.edit().putString(CURRENT_USER, "").apply()
    }

    companion object {
        private const val CURRENT_USER = "getCurrentUser"
    }
}