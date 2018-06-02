package com.catalin.mymedic.storage.repository

import com.catalin.mymedic.data.AvailableAppointments
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.storage.source.MedicalAppointmentsFirebaseSource
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/29/2018
 */
class MedicalAppointmentsRepository @Inject constructor(private val medicalAppointmentsRemoteSource: MedicalAppointmentsFirebaseSource) {

    private val availableAppointmentsDetails = HashMap<String, AvailableAppointments>()

    fun createAppointment(appointment: MedicalAppointment) = medicalAppointmentsRemoteSource.createMedicalAppointment(appointment)

    fun getMedicalAppointmentsForMedic(medicId: String) = medicalAppointmentsRemoteSource.getMedicalAppointmentsForMedic(medicId)

    fun getAvailableAppointmentsTime(medicId: String) = medicalAppointmentsRemoteSource.getAvailableAppointmentsTime(medicId)
}