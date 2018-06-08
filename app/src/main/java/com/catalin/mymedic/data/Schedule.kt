package com.catalin.mymedic.data

/**
 * @author catalinradoiu
 * @since 6/1/2018
 */
data class Schedule(val dayName: String, val start: Int, val end: Int) {

    /*
        Empty constructor needed for parsing the firebase json to an actual MedicalSpecialty object
        DataSnapshot.getValue(Schedule::class.java)
     */
    constructor() : this("", 0, 0)
}