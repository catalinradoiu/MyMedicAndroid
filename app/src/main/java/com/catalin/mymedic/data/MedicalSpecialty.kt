package com.catalin.mymedic.data

/**
 * @author catalinradoiu
 * @since 5/23/2018
 */
data class MedicalSpecialty(val id: Int, val name: String, val imageUrl: String) {
    /*
        Empty constructor needed for parsing the firebase json to an actual MedicalSpecialty object
        DataSnapshot.getValue(MedicalSpecialty::class.java)
     */
    constructor() : this(0, "", "")
}