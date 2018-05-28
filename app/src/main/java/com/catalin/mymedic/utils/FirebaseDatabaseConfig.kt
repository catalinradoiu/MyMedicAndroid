package com.catalin.mymedic.utils

/**
 * @author catalinradoiu
 * @since 2/13/2018
 */
class FirebaseDatabaseConfig {

    companion object {

        //Defaults
        const val DEFAULT_USER_IMAGE_LOCATION = "users/anonymous_person.png"

        //Users table
        const val USERS_TABLE_NAME = "users"
        const val USERS_TABLE_SPECIALISATION_ID_COLUMN = "specialisationId"

        //Medical specialties table
        const val MEDICAL_SPECIALTIES_TABLE_NAME = "medical_specialties"
    }
}