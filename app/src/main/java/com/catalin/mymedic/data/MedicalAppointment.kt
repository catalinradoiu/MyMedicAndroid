package com.catalin.mymedic.data

/**
 * @author catalinradoiu
 * @since 5/29/2018
 */
data class MedicalAppointment(
    val id: String,
    val dateTime: Long,
    val patientId: String,
    val medicId: String,
    val description: String,
    val status: AppointmentStatus
) {

    /*
        Empty constructor needed for parsing the firebase json to an actual MedicalAppointment object
        DataSnapshot.getValue(MedicalAppointment::class.java)
     */
    constructor() : this("", 0, "", "", "", AppointmentStatus.AWAITING)
}

enum class AppointmentStatus {
    CONFIRMED, AWAITING, REJECTED
}