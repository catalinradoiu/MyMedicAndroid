package com.catalin.mymedic.data

/**
 *  Class for holding the user data
 */
data class User(
    var displayName: String,
    var email: String,
    var birthDate: Long,
    var gender: Gender,
    var userRole: Role,
    var specialisation: String
) {
    constructor() : this("", "", 0, Gender.NOT_COMPLETED, Role.PATIENT, "")
}

enum class Gender {
    MALE, FEMALE, NOT_COMPLETED
}

enum class Role {
    PATIENT, MEDIC
}