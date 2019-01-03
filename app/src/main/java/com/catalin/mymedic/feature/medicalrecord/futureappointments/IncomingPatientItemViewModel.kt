package com.catalin.mymedic.feature.medicalrecord.futureappointments

import android.databinding.ObservableField
import android.databinding.ObservableLong

/**
 * @author catalinradoiu
 * @since 6/25/2018
 */
class IncomingPatientItemViewModel {

    val patientName = ObservableField<String>("")
    val appointmentTime = ObservableLong(0)
}