package com.catalin.mymedic.storage.source

import com.catalin.mymedic.data.User
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseAuth
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote source for the users based on Firebase
 *
 * @author catalinradoiu
 * @since 2/21/2018
 */
@Singleton
class UsersFirebaseSource @Inject constructor(private val firebaseAuth: FirebaseAuth, private val firebaseDatabase: FirebaseDatabase) {

    /**
     * Get as parameters the user and his password
     * Registers the user then sends the corresponding email
     * @return a Completable with the status of the operation
     */
    fun registerUser(user: User, password: String): Completable =
        RxFirebaseAuth.createUserWithEmailAndPassword(firebaseAuth, user.email, password).flatMapCompletable {
            it.user.sendEmailVerification()
            RxFirebaseDatabase.setValue(
                firebaseDatabase.getReference(FirebaseDatabaseConfig.USERS_TABLE_NAME).child(it.user.uid),
                user.apply {
                    id = it.user.uid
                }
            )
        }

    fun getUserByEmailAndPassword(email: String, password: String): Single<AuthResult> =
        RxFirebaseAuth.signInWithEmailAndPassword(firebaseAuth, email, password).toSingle()

    fun sendPasswordResetEmail(email: String) = RxFirebaseAuth.sendPasswordResetEmail(firebaseAuth, email)

    fun getCurrentUser(): FirebaseUser? = firebaseAuth.currentUser

    fun getUserById(userId: String): Single<User> =
        RxFirebaseDatabase.observeSingleValueEvent(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.USERS_TABLE_NAME).child(userId),
            User::class.java
        ).toSingle()

    fun getMedicsBySpecialty(specialtyId: Int, currentUserId: String): Single<List<User>> =
        RxFirebaseDatabase.observeSingleValueEvent(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.USERS_TABLE_NAME).orderByChild(
                FirebaseDatabaseConfig.USERS_TABLE_SPECIALISATION_ID_COLUMN
            ).equalTo(
                specialtyId.toDouble()
            ), { data ->
                data.children.mapNotNull { value -> value.getValue(User::class.java) }
                    .filter { medic -> medic.specialisationId == specialtyId && medic.id != currentUserId }
            }).toSingle()

    fun updateUserNotificationToken(token: String?, userId: String) {
        RxFirebaseDatabase.setValue(firebaseDatabase.reference.child(userId).child(FirebaseDatabaseConfig.USERS_NOTIFICATION_TOKEN), token)
    }
}
