package com.catalin.mymedic.data

/**
 * @author catalinradoiu
 * @since 5/29/2018
 */
data class MedicalAppointment(
    var id: String,
    val patientName: String,
    val dateTime: Long,
    val patientId: String,
    val medicId: String,
    val description: String,
    var status: AppointmentStatus
) {

    /*
        Empty constructor needed for parsing the firebase json to an actual MedicalAppointment object
        DataSnapshot.getValue(MedicalAppointment::class.java)
     */
    constructor() : this("", "", 0, "", "", "", AppointmentStatus.AWAITING)
}

enum class AppointmentStatus {
    CONFIRMED, AWAITING, REJECTED, CANCELED_BY_PATIENT, CANCELED_BY_MEDIC
}

fun MedicalAppointment.toMap() = HashMap<String, Any>().apply {
    put("id", id)
    put("patientName", patientName)
    put("patientId", patientId)
    put("status", status)
    put("medicId", medicId)
    put("description", description)
    put("dateTime", dateTime)
}