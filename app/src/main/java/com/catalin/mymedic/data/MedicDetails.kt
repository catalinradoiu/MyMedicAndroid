package com.catalin.mymedic.data

/**
 * @author catalinradoiu
 * @since 6/1/2018
 */
data class MedicDetails(val id: String, val description: String, var schedule: List<Schedule>, val appointmentDuration: Int) {

    constructor() : this("", "", ArrayList<Schedule>(), 0)
}