package com.catalin.mymedic.storage.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.catalin.mymedic.data.Role
import javax.inject.Inject

/**
 * Class for managing the shared preferences
 *
 * @author catalinradoiu
 * @since 4/15/2018
 */
class SharedPreferencesManager @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setCurrentUserRole(userRole: Role) {
        sharedPreferences.edit().putString(CURRENT_USER_ROLE, userRole.toString()).apply()
    }

    fun getCurrentUserRole(): Role = if (sharedPreferences.getString(CURRENT_USER_ROLE, ROLE_PATIENT) == SharedPreferencesManager.Companion.ROLE_PATIENT)
        Role.PATIENT else Role.MEDIC

    companion object {
        private const val CURRENT_USER_ROLE = "currentUserRole"
        private const val ROLE_PATIENT = "PATIENT"
        private const val ROLE_MEDIC = "MEDIC"
    }
}