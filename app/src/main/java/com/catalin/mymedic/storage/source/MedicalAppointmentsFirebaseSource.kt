package com.catalin.mymedic.storage.source

import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.google.firebase.database.FirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/29/2018
 */
class MedicalAppointmentsFirebaseSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    fun createMedicalAppointment(medicalAppointment: MedicalAppointment): Completable {
        val id: String = firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).push().key ?: ""
        return RxFirebaseDatabase.setValue(firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).child(id),
            medicalAppointment.apply {
                this.id = id
            }
        )
    }


}

