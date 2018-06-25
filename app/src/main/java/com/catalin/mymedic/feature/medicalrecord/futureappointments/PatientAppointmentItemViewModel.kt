package com.catalin.mymedic.feature.medicalrecord.futureappointments

import android.databinding.ObservableField
import android.databinding.ObservableLong
import com.catalin.mymedic.data.AppointmentStatus

/**
 * View model class for the patient own appointment view holder item
 * @author catalinradoiu
 * @since 6/17/2018
 */
class PatientAppointmentItemViewModel {

    val appointmentTime = ObservableLong(0)
    val medicName = ObservableField<String>("")
    val specialtyName = ObservableField<String>("")
    val appointmentStatusString = ObservableField<String>("")
    val status = ObservableField<AppointmentStatus>(AppointmentStatus.AWAITING)
}