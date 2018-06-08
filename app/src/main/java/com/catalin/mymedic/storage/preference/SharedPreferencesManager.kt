package com.catalin.mymedic.storage.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Class for managing the shared preferences
 *
 * @author catalinradoiu
 * @since 4/15/2018
 */
@Singleton
class SharedPreferencesManager @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    var currentUserSpecialty by PreferenceFieldDelegate.Int(CURRENT_USER_SPECIALTY)
    var currentUserName by PreferenceFieldDelegate.String(CURRENT_USER_NAME)
    var currentUserId by PreferenceFieldDelegate.String(CURRENT_USER_ID)

    fun clearUserPreferences() {
        currentUserName = PREFERENCE_DEFAULT_STRING_VALUE
        currentUserId = PREFERENCE_DEFAULT_STRING_VALUE
        currentUserSpecialty = PREFERENCE_DEFAULT_INT_VALUE
    }

    private sealed class PreferenceFieldDelegate<T>(protected val key: kotlin.String) : ReadWriteProperty<SharedPreferencesManager, T> {

        class Int(key: kotlin.String) : PreferenceFieldDelegate<kotlin.Int>(key) {
            override fun getValue(thisRef: SharedPreferencesManager, property: KProperty<*>): kotlin.Int =
                thisRef.sharedPreferences.getInt(key, PREFERENCE_DEFAULT_INT_VALUE)

            override fun setValue(thisRef: SharedPreferencesManager, property: KProperty<*>, value: kotlin.Int) =
                thisRef.sharedPreferences.edit().putInt(key, value).apply()

        }

        class String(key: kotlin.String) : PreferenceFieldDelegate<kotlin.String>(key) {
            override fun getValue(thisRef: SharedPreferencesManager, property: KProperty<*>): kotlin.String =
                thisRef.sharedPreferences.getString(key, PREFERENCE_DEFAULT_STRING_VALUE)

            override fun setValue(thisRef: SharedPreferencesManager, property: KProperty<*>, value: kotlin.String) =
                thisRef.sharedPreferences.edit().putString(key, value).apply()

        }
    }

    companion object {
        private const val CURRENT_USER_SPECIALTY = "currentUserSpecialty"
        private const val CURRENT_USER_NAME = "currentUserName"
        private const val CURRENT_USER_ID = "currentUserId"
        private const val PREFERENCE_DEFAULT_INT_VALUE = 0
        private const val PREFERENCE_DEFAULT_STRING_VALUE = ""
    }
}