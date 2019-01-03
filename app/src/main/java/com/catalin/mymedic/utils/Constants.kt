package com.catalin.mymedic.utils

/**
 * @author catalinradoiu
 * @since 4/14/2018
 */
class Constants {

    companion object {
        const val PATIENT = 0

        const val MONDAY = "Monday"
        const val TUESDAY = "Tuesday"
        const val WEDNESDAY = "Wednesday"
        const val THURSDAY = "Thursday"
        const val FRIDAY = "Friday"

        const val TOW_MONTHS_TIME_IN_MILLIS: Long = 2 * 31 * 24 * 3600 * 1000L
        const val HOUR_TIME_IN_MILLIS: Long = 3600 * 1000
        const val MINUTE_TIME_IN_MILLIS = 60 * 1000
        const val DAY_TIME_IN_MILLIS = 24 * 3600 * 1000

        const val OWN_APPOINTMENTS_HEADER_ID = "ownAppointmentHeaderItem"
        const val PATIENT_APPOINTMENTS_HEADER_ID = "patientAppointmentsHeaderItem"
    }
}