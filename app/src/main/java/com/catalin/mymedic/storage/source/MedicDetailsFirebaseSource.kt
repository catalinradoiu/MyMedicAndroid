package com.catalin.mymedic.storage.source

import com.catalin.mymedic.data.MedicDetails
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.google.firebase.database.FirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author catalinradoiu
 * @since 6/1/2018
 */
@Singleton
class MedicDetailsFirebaseSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    fun getDetailsForMedic(medicId: String): Single<MedicDetails> =
        RxFirebaseDatabase.observeSingleValueEvent(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICS_DETAILS_TABLE).child(medicId),
            MedicDetails::class.java
        ).toSingle()
}