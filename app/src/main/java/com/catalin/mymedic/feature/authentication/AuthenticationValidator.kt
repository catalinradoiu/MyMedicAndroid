package com.catalin.mymedic.feature.authentication

import android.util.Patterns
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 2/28/2018
 */
class AuthenticationValidator @Inject constructor() {

    fun isValidName(name: String) = name.matches(Regex("[a-zA-Z]+")) && name.length > 1

    fun isValidPassword(password: String): Boolean = password.length >= 8 //&& password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+\$"))

    fun isValidEmailAdress(emailAddress: String) = Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches() && !emailAddress.isEmpty()

    fun isValidBirthDate() {

    }
}