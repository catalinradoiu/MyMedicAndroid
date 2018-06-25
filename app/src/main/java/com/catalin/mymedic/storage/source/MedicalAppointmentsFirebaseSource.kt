package com.catalin.mymedic.storage.source

import com.catalin.mymedic.data.*
import com.catalin.mymedic.utils.Constants
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.catalin.mymedic.utils.extension.setToDayStart
import com.catalin.mymedic.utils.extension.setToDayStartWithTimestamp
import com.google.firebase.database.FirebaseDatabase
import com.wdullaer.materialdatetimepicker.time.Timepoint
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * @author catalinradoiu
 * @since 5/29/2018
 */
@Singleton
class MedicalAppointmentsFirebaseSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    fun createMedicalAppointment(medicalAppointment: MedicalAppointment): Completable {
        val id: String = firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).push().key ?: ""
        return RxFirebaseDatabase.setValue(firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).child(id),
            medicalAppointment.apply {
                this.id = id
            }
        )
    }

    fun getAwaitingMedicalAppointmentsForMedic(medicId: String): Flowable<List<MedicalAppointment>> =
        RxFirebaseDatabase.observeValueEvent(firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).orderByChild(
            FirebaseDatabaseConfig.APPOINTMENTS_TABLE_MEDIC_ID
        ).equalTo(medicId),
            { data ->
                data.children.mapNotNull { value ->
                    value.getValue(MedicalAppointment::class.java)
                }.filter { it.status == AppointmentStatus.AWAITING }.sortedBy { it.dateTime }

            }
        )

    fun getFutureMedicalAppointments(userId: String, timestamp: Long): Flowable<List<MedicalAppointment>> =
        RxFirebaseDatabase.observeValueEvent(firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).orderByChild(
            FirebaseDatabaseConfig.APPOINTMENTS_TABLE_MEDIC_ID
        )
            .equalTo(userId), { data ->
            data.children.mapNotNull { value -> value.getValue(MedicalAppointment::class.java) }
                .filter { (it.dateTime >= timestamp) && (it.status == AppointmentStatus.AWAITING || it.status == AppointmentStatus.CONFIRMED) }
                .sortedBy { it.dateTime }
        }).flatMap { firstResult ->
            val result = ArrayList<MedicalAppointment>().apply {
                if (firstResult.isNotEmpty()) {
                    add(MedicalAppointment().apply { id = Constants.PATIENT_APPOINTMENTS_HEADER_ID })
                    addAll(firstResult)
                }
            }
            RxFirebaseDatabase.observeValueEvent(firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).orderByChild(
                FirebaseDatabaseConfig.APPOINTMENT_PATIENT_ID
            )
                .equalTo(userId), { snapshot ->
                snapshot.children.mapNotNull { value -> value.getValue(MedicalAppointment::class.java) }
                    .filter { (it.dateTime >= timestamp) && (it.status == AppointmentStatus.AWAITING || it.status == AppointmentStatus.CONFIRMED) }
                    .sortedBy { it.dateTime }
            }).map { secondResult ->
                if (secondResult.isNotEmpty()) {
                    result.add(MedicalAppointment().apply { id = Constants.OWN_APPOINTMENTS_HEADER_ID })
                    result.addAll(secondResult)
                }
                result
            }
        }

    fun updateMedicalAppointment(appointment: MedicalAppointment) =
        RxFirebaseDatabase.updateChildren(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).child(
                appointment.id
            ),
            appointment.toMap()
        )

    fun createCancelationReason(cancelationReason: AppointmentCancelationReason): Completable
//            : Completable = RxFirebaseDatabase.setValue(
//        firebaseDatabase.reference.child(FirebaseDatabaseConfig.CANCELED_APPOINTMENTS).child(cancelationReason.id),
//        cancelationReason
//    )
    {
        firebaseDatabase.reference.child(FirebaseDatabaseConfig.CANCELED_APPOINTMENTS).child(cancelationReason.id).setValue(cancelationReason)
        return Completable.complete()
    }


    fun getFutureMedicalAppointmentsForPatient(userId: String, timestamp: Long): Flowable<List<MedicalAppointment>> =
        RxFirebaseDatabase.observeValueEvent(firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).orderByChild(
            FirebaseDatabaseConfig.APPOINTMENT_PATIENT_ID
        ).equalTo(userId),
            { data ->
                data.children.mapNotNull { value -> value.getValue(MedicalAppointment::class.java) }.filter { it.dateTime >= timestamp }
            }
        )

    fun getAvailableAppointmentsTime(medicId: String): Single<AvailableAppointments> {
        val result = AvailableAppointments()
        return RxFirebaseDatabase.observeSingleValueEvent(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICS_DETAILS_TABLE).child(medicId),
            MedicDetails::class.java
        ).toSingle().flatMap { medicDetails ->
            result.apply {
                medicSchedule = medicDetails.schedule
                appointmentDuration = medicDetails.appointmentDuration
                defaultSelectableTimeForWeek = createDefaultAppointmentDaysForWeek(medicSchedule, appointmentDuration)
            }
            RxFirebaseDatabase.observeSingleValueEvent(
                firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).orderByChild(
                    FirebaseDatabaseConfig.APPOINTMENTS_TABLE_MEDIC_ID
                )
            ).map { data ->
                val medicalAppointments = ArrayList<MedicalAppointment>(data.children.mapNotNull { value ->
                    value.getValue(MedicalAppointment::class.java)
                }.filter { it.status != AppointmentStatus.REJECTED })
                medicalAppointments.sortBy { it.dateTime }
                medicalAppointments.forEach { appointment ->
                    val calendar = Calendar.getInstance().setToDayStartWithTimestamp(appointment.dateTime)
                    if (result.unselectableTimesForDays[calendar.timeInMillis] == null) {
                        result.unselectableTimesForDays[calendar.timeInMillis] = ArrayList()
                    }
                    val currentAppointmentCalendar = Calendar.getInstance().apply {
                        timeInMillis = appointment.dateTime
                    }
                    result.unselectableTimesForDays[calendar.timeInMillis]?.add(
                        Timepoint(currentAppointmentCalendar.get(Calendar.HOUR_OF_DAY), currentAppointmentCalendar.get(Calendar.MINUTE))
                    )
                }
                result.apply {
                    unselectableDays = getUnavailableDays(defaultSelectableTimeForWeek, unselectableTimesForDays)
                }
            }.toSingle()
        }
    }

    private fun createDefaultAppointmentDaysForWeek(medicSchedule: List<Schedule>, appointmentTime: Int): HashMap<Int, ArrayList<Timepoint>> {
        val defaultAppointmentTime = HashMap<Int, ArrayList<Timepoint>>()
        medicSchedule.forEach { schedule ->
            val dayCode = when (schedule.dayName) {
                Constants.MONDAY -> Calendar.MONDAY
                Constants.TUESDAY -> Calendar.TUESDAY
                Constants.WEDNESDAY -> Calendar.WEDNESDAY
                Constants.THURSDAY -> Calendar.THURSDAY
                Constants.FRIDAY -> Calendar.FRIDAY
                else -> WEEKEND_DAY
            }
            val dayHours = ArrayList<Timepoint>()
            if (dayCode != WEEKEND_DAY) {
                val appointmentDurationLong = appointmentTime * Constants.MINUTE_TIME_IN_MILLIS
                var currentTime: Long = schedule.start * Constants.HOUR_TIME_IN_MILLIS
                val endTime = schedule.end * Constants.HOUR_TIME_IN_MILLIS
                while (currentTime < endTime) {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = currentTime
                    }
                    dayHours.add(Timepoint(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)))
                    currentTime += appointmentDurationLong
                }
                defaultAppointmentTime[dayCode] = dayHours
            }
        }
        return defaultAppointmentTime
    }


    private fun getUnavailableDays(
        defaultAvailableTimes: HashMap<Int, ArrayList<Timepoint>>,
        unavailableTimes: HashMap<Long, ArrayList<Timepoint>>
    ): ArrayList<Calendar> {
        // First get a list with the weekend days
        val unavailableDays = ArrayList<Calendar>()
        val currentTime = System.currentTimeMillis()

        val iterationCalendar = Calendar.getInstance().setToDayStart()

        while (iterationCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            iterationCalendar.timeInMillis += Constants.DAY_TIME_IN_MILLIS
        }

        while (iterationCalendar.timeInMillis <= currentTime + Constants.TOW_MONTHS_TIME_IN_MILLIS) {
            val saturdayCalendar = Calendar.getInstance().apply {
                timeInMillis = iterationCalendar.timeInMillis

            }
            unavailableDays.add(saturdayCalendar)
            iterationCalendar.timeInMillis += Constants.DAY_TIME_IN_MILLIS
            val sundayCalendar = Calendar.getInstance().setToDayStartWithTimestamp(iterationCalendar.timeInMillis)
            unavailableDays.add(sundayCalendar)
            iterationCalendar.timeInMillis += Constants.DAY_TIME_IN_MILLIS * 6
        }

        for ((key, value) in unavailableTimes) {
            val scheduleForDay = determineSchedule(key, defaultAvailableTimes)
            scheduleForDay?.let {
                if (value.containsAll(it)) {
                    unavailableDays.add(Calendar.getInstance().setToDayStartWithTimestamp(key))
                }
            }
        }

        return unavailableDays
    }

    private fun determineSchedule(dayStart: Long, defaultAvailableTimes: HashMap<Int, ArrayList<Timepoint>>): ArrayList<Timepoint>? {
        val dayCode = Calendar.getInstance().apply {
            timeInMillis = dayStart
        }.get(Calendar.DAY_OF_WEEK)

        return defaultAvailableTimes[dayCode]
    }

    companion object {
        private const val WEEKEND_DAY = -1
    }
}

