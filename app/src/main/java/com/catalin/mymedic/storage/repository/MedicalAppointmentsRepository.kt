package com.catalin.mymedic.storage.repository

import com.catalin.mymedic.data.AvailableAppointments
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.storage.source.MedicalAppointmentsFirebaseSource
import com.wdullaer.materialdatetimepicker.time.Timepoint
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author catalinradoiu
 * @since 5/29/2018
 */
@Singleton
class MedicalAppointmentsRepository @Inject constructor(private val medicalAppointmentsRemoteSource: MedicalAppointmentsFirebaseSource) {

    private val availableAppointmentsDetails = HashMap<String, AvailableAppointments>()

    fun createAppointment(appointment: MedicalAppointment, appointmentDayTime: Pair<Long, Timepoint>) =
        medicalAppointmentsRemoteSource.createMedicalAppointment(appointment).also {
            availableAppointmentsDetails[appointment.medicId]?.unselectableTimesForDays?.let {
                it[appointmentDayTime.first]?.add(appointmentDayTime.second)
            }
        }

    fun getMedicalAppointmentsForMedic(medicId: String) = medicalAppointmentsRemoteSource.getMedicalAppointmentsForMedic(medicId)

    fun getAvailableAppointmentsTime(medicId: String): Single<AvailableAppointments> =
        if (availableAppointmentsDetails[medicId] == null)
            medicalAppointmentsRemoteSource.getAvailableAppointmentsTime(medicId).doOnSuccess {
                availableAppointmentsDetails[medicId] = it
            }
        else
            Single.just(availableAppointmentsDetails[medicId])
}