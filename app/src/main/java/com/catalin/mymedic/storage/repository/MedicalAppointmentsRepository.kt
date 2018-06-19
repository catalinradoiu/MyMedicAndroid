package com.catalin.mymedic.storage.repository

import com.catalin.mymedic.data.AppointmentCancelationReason
import com.catalin.mymedic.data.AvailableAppointments
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.storage.source.MedicalAppointmentsFirebaseSource
import com.wdullaer.materialdatetimepicker.time.Timepoint
import io.reactivex.Completable
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

    fun getAwaitingAppointmentsForMedic(medicId: String) =
        medicalAppointmentsRemoteSource.getAwaitingMedicalAppointmentsForMedic(medicId)

    fun getFutureMedicalAppointmentsForUser(userId: String, timestamp: Long) =
        medicalAppointmentsRemoteSource.getFutureMedicalAppointmentsForUser(userId, timestamp)

    fun getAvailableAppointmentsTime(medicId: String): Single<AvailableAppointments> =
        if (availableAppointmentsDetails[medicId] == null)
            medicalAppointmentsRemoteSource.getAvailableAppointmentsTime(medicId).doOnSuccess {
                availableAppointmentsDetails[medicId] = it
            }
        else
            Single.just(availableAppointmentsDetails[medicId])

    fun updateMedicalAppointment(appointment: MedicalAppointment): Completable =
        medicalAppointmentsRemoteSource.updateMedicalAppointment(appointment)

    //TODO : Completable not working here, look into it
    fun createCancelationReason(appointmentCancelationReason: AppointmentCancelationReason) =
        medicalAppointmentsRemoteSource.createCancelationReason(appointmentCancelationReason)
}