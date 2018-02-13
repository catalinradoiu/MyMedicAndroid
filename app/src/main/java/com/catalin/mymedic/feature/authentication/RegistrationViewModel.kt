package com.catalin.mymedic.feature.authentication

import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 2/12/2018
 */
class RegistrationViewModel @Inject constructor(private val firebaseAuth: FirebaseAuth) {

    fun registerUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener({ result ->
                    if (result.isSuccessful) {

                    } else {

                    }
                })
    }
}