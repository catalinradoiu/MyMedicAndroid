package com.catalin.mymedic.feature.medicalrecord.medicalhistory

import android.databinding.ObservableField
import android.databinding.ObservableLong
import com.catalin.mymedic.data.AppointmentStatus

/**
 * @author catalinradoiu
 * @since 6/26/2018
 */
class PastPatientItemViewModel {

    val patientName = ObservableField<String>("")
    val appointmentDate = ObservableLong(0)
    val appointmentStatus = ObservableField<AppointmentStatus>(AppointmentStatus.CONFIRMED)
}