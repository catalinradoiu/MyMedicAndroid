package com.catalin.mymedic.storage.source

import com.catalin.mymedic.data.User
import com.catalin.mymedic.utils.DatabaseConfig
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

/**
 * Remote source for the users based on Firebase
 *
 * @author catalinradoiu
 * @since 2/21/2018
 */

class UsersFirebaseSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) {

    /**
     * Get as parameters the user and his password
     * Registers the user then sends the corresponding email
     * @return a Completable with the status of the operation
     */
    fun registerUser(user: User, password: String): Completable =
        RxFirebaseAuth.createUserWithEmailAndPassword(firebaseAuth, user.email, password).flatMapCompletable {
            it.user.sendEmailVerification()
            RxFirebaseDatabase.setValue(
                firebaseDatabase.getReference(DatabaseConfig.USERS_TABLE_NAME).child(it.user.uid),
                user
            )
        }

    fun getUserByEmailAndPassword(email: String, password: String): Single<AuthResult> =
        RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, email, password).toSingle()

    fun sendPasswordResetEmail(email: String) = RxFirebaseAuth.sendPasswordResetEmail(firebaseAuth, email)

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun getUserById(userId: String): Single<User> =
        RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.reference.child(DatabaseConfig.USERS_TABLE_NAME).child(userId), User::class.java).toSingle()

    fun getMedicsBySpecialty(specialtyId: Int): Single<List<User>> =
        RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.reference.child(DatabaseConfig.USERS_TABLE_NAME).orderByChild("specialisationId").equalTo(
            specialtyId.toDouble()
        ), { data ->
            data.children.mapNotNull { value -> value.getValue(User::class.java) }.filter { medic -> medic.specialisationId == specialtyId }
        }).toSingle()
}
