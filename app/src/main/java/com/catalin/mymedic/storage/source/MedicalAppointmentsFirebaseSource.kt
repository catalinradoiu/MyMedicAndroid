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

        val iterationCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        while (iterationCalendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
            iterationCalendar.timeInMillis += Constants.DAY_TIME_IN_MILLIS
        }

        while (iterationCalendar.timeInMillis <= currentTime + Constants.TOW_MONTHS_TIME_IN_MILLIS) {
            val saturdayCalendar = Calendar.getInstance().apply {
                timeInMillis = iterationCalendar.timeInMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            unavailableDays.add(saturdayCalendar)
            iterationCalendar.timeInMillis += Constants.DAY_TIME_IN_MILLIS
            val sundayCalendar = Calendar.getInstance().apply {
                timeInMillis = iterationCalendar.timeInMillis
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }
            unavailableDays.add(sundayCalendar)
            iterationCalendar.timeInMillis += Constants.DAY_TIME_IN_MILLIS * 6
        }

//        unavailableTimes.forEach { entry ->
//            val entryDay = Calendar.getInstance().apply {
//                timeInMillis = entry.key
//            }
//            when (entryDay.get(Calendar.DAY_OF_WEEK)) {
//                Calendar.MONDAY -> defaultAvailableTimes[]
//            }
//        }

        return unavailableDays
    }

    companion object {
        private const val WEEKEND_DAY = -1
    }

}

