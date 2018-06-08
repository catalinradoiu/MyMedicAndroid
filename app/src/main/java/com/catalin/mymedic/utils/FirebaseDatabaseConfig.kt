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

        //Medics details table
        const val MEDICS_DETAILS_TABLE = "medics_details"

        //Medical specialties table
        const val MEDICAL_SPECIALTIES_TABLE_NAME = "medical_specialties"

        //Appointments table
        const val MEDICAL_APPOINTMENTS_TABLE_NAME = "medical_appointments"
        const val APPOINTMENTS_TABLE_MEDIC_ID = "medicId"
    }
}