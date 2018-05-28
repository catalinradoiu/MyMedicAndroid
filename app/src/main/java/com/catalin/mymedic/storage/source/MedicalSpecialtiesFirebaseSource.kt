package com.catalin.mymedic.storage.source

import com.catalin.mymedic.data.MedicalSpecialty
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.google.firebase.database.FirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/23/2018
 */
class MedicalSpecialtiesFirebaseSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    fun getAllSpecialties(): Single<List<MedicalSpecialty>> =
        RxFirebaseDatabase.observeSingleValueEvent(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_SPECIALTIES_TABLE_NAME),
            { data ->
                data.children.mapNotNull { value ->
                    value.getValue(MedicalSpecialty::class.java)
                }
            }).toSingle()
}