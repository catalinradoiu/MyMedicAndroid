package com.catalin.mymedic.storage.source

import com.catalin.mymedic.data.Gender
import com.catalin.mymedic.data.User
import com.catalin.mymedic.utils.DatabaseConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 2/21/2018
 */

class UsersRemoteSource @Inject constructor(private val firebaseAuth: FirebaseAuth,
                                            private val firebaseDatabase: FirebaseDatabase){

    fun registerUser(email: String, password: String, firstName: String, lastName: String, birthDate: Long, gender: Gender): Completable =
            RxFirebaseAuth.createUserWithEmailAndPassword(firebaseAuth, email, password)
                    .flatMapCompletable {
                        it.user.sendEmailVerification()
                        RxFirebaseDatabase.setValue(firebaseDatabase.getReference(DatabaseConfig.DATABASE_NAME).child(DatabaseConfig.USERS_TABLE_NAME).push(),
                                User(firstName, lastName, email, birthDate, gender))
                    }
}
