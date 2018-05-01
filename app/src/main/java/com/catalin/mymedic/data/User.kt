package com.catalin.mymedic.data

/**
 *  Class for holding the user data
 */
data class User(
    var firstName: String,
    var lastName: String,
    var email: String,
    var birthDate: Long,
    var gender: Gender,
    var userRole: Role,
    var specialisation: String
)

enum class Gender {
    MALE, FEMALE, NOT_COMPLETED
}

enum class Role {
    PATIENT, MEDIC
}