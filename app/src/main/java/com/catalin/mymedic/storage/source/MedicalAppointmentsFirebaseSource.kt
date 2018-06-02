package com.catalin.mymedic.storage.source

import com.catalin.mymedic.data.AvailableAppointments
import com.catalin.mymedic.data.MedicDetails
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.data.Schedule
import com.catalin.mymedic.utils.Constants
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.google.firebase.database.FirebaseDatabase
import com.wdullaer.materialdatetimepicker.time.Timepoint
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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

    fun getMedicalAppointmentsForMedic(medicId: String): Single<List<MedicalAppointment>> =
        RxFirebaseDatabase.observeSingleValueEvent(firebaseDatabase.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).orderByChild(
            FirebaseDatabaseConfig.APPOINTMENTS_TABLE_MEDIC_ID
        ),
            { data ->
                data.children.mapNotNull { value ->
                    value.getValue(MedicalAppointment::class.java)
                }
            }
        ).toSingle()

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
                })
                medicalAppointments.sortBy { it.dateTime }
                medicalAppointments.forEach { appointment ->
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = appointment.dateTime
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    if (result.unselectableTimesForDays[calendar.timeInMillis] == null) {
                        result.unselectableTimesForDays[calendar.timeInMillis] = ArrayList()
                    }
                    val currentAppointmentCalendar = Calendar.getInstance().apply {
                        timeInMillis = appointment.dateTime
                    }
                    result.unselectableTimesForDays[calendar.timeInMillis]?.add(
                        Timepoint(
                            currentAppointmentCalendar.get(Calendar.HOUR_OF_DAY),
                            currentAppointmentCalendar.get(Calendar.MINUTE)
                        )
                    )
                }
                result
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
                val appointmentDurationLong = appointmentTime * MINUTE_MILLISECONDS_FACTOR
                var currentTime: Long = schedule.start * ONE_HOUR_MILLISECONDS_FACTOR.toLong()
                val endTime = schedule.end * ONE_HOUR_MILLISECONDS_FACTOR
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

    private fun determineDayStartEndTime(calendar: Calendar, medicSchedule: List<Schedule>): Pair<Long, Long> {
        calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
        }
        var endTime: Long = WEEKEND_DAY_TIME
        val startTime: Long =
            when (calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar.MONDAY -> medicSchedule.find { day -> day.dayName == Constants.MONDAY }?.let {
                    endTime = calendar.timeInMillis + it.end * ONE_HOUR_MILLISECONDS_FACTOR
                    calendar.timeInMillis + it.start * ONE_HOUR_MILLISECONDS_FACTOR
                } ?: INVALID_TIME
                Calendar.TUESDAY -> medicSchedule.find { day -> day.dayName == Constants.TUESDAY }?.let {
                    endTime = calendar.timeInMillis + it.end * ONE_HOUR_MILLISECONDS_FACTOR
                    calendar.timeInMillis + it.start * ONE_HOUR_MILLISECONDS_FACTOR
                } ?: INVALID_TIME
                Calendar.WEDNESDAY -> medicSchedule.find { day -> day.dayName == Constants.WEDNESDAY }?.let {
                    endTime = calendar.timeInMillis + it.end * ONE_HOUR_MILLISECONDS_FACTOR
                    calendar.timeInMillis + it.start * ONE_HOUR_MILLISECONDS_FACTOR
                } ?: INVALID_TIME
                Calendar.THURSDAY -> medicSchedule.find { day -> day.dayName == Constants.THURSDAY }?.let {
                    endTime = calendar.timeInMillis + it.end * ONE_HOUR_MILLISECONDS_FACTOR
                    calendar.timeInMillis + it.start * ONE_HOUR_MILLISECONDS_FACTOR
                } ?: INVALID_TIME
                Calendar.FRIDAY -> medicSchedule.find { day -> day.dayName == Constants.FRIDAY }?.let {
                    endTime = calendar.timeInMillis + it.end * ONE_HOUR_MILLISECONDS_FACTOR
                    calendar.timeInMillis + it.start * ONE_HOUR_MILLISECONDS_FACTOR
                } ?: INVALID_TIME
                else -> WEEKEND_DAY_TIME
            }
        return Pair(startTime, endTime)
    }

    companion object {
        private const val ONE_DAY_TIMESTAMP = 3600 * 24 * 1000
        private const val ONE_HOUR_MILLISECONDS_FACTOR = 3600 * 1000
        private const val MINUTE_MILLISECONDS_FACTOR = 60 * 1000
        private const val WEEKEND_DAY_TIME = 0L
        private const val INVALID_TIME = -1L
        private const val NO_APPOINTMENT_KEY = -1L
        private const val WEEKEND_DAY = -1
    }

}

