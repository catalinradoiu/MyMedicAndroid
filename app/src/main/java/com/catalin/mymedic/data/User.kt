package com.catalin.mymedic.data

/**
 *  Class for holding the user data
 */
data class User(
    var displayName: String,
    var email: String,
    var birthDate: Long,
    var gender: Gender,
    var specialisationId: Int,
    var imageUrl: String
) {
    constructor() : this("", "", 0, Gender.NOT_COMPLETED, 0, "")
}

enum class Gender {
    MALE, FEMALE, NOT_COMPLETED
}