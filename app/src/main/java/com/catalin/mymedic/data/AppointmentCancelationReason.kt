package com.catalin.mymedic.data

/**
 * @author catalinradoiu
 * @since 6/19/2018
 */
data class AppointmentCancelationReason(val id: String, val reason: String, val cancelationType: AppointmentStatus) {

    constructor() : this("", "", AppointmentStatus.REJECTED)
}