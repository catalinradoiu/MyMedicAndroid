package com.catalin.mymedic.storage.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.catalin.mymedic.utils.Constants
import javax.inject.Inject

/**
 * Class for managing the shared preferences
 *
 * @author catalinradoiu
 * @since 4/15/2018
 */
class SharedPreferencesManager @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setCurrentUserSpecialty(userSpecialty: Int) {
        sharedPreferences.edit().putInt(CURRENT_USER_ROLE, userSpecialty).apply()
    }

    fun getCurrentUserRole(): Int = sharedPreferences.getInt(CURRENT_USER_ROLE, Constants.PATIENT)

    companion object {
        private const val CURRENT_USER_ROLE = "currentUserRole"
    }
}