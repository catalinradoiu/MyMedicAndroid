package com.catalin.mymedic.data

import java.util.*

/**
 *
 */
data class User(
        var firstName: String,
        var lastName: String,
        var email: String,
        var birthDate: Long,
        var gender: Gender
)

enum class Gender {
    MALE, FEMALE
}