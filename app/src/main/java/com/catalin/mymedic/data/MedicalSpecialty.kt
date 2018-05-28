package com.catalin.mymedic.data

/**
 * @author catalinradoiu
 * @since 5/23/2018
 */
data class MedicalSpecialty(val id: Int, val name: String, val imageUrl: String) {
    constructor() : this(0, "", "")
}