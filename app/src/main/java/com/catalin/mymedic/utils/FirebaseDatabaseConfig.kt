package com.catalin.mymedic.utils

/**
 * @author catalinradoiu
 * @since 2/13/2018
 */
class FirebaseDatabaseConfig {

    companion object {

        const val USERS_IMAGES_FOLDER = "users/"
        const val USER_IMAGE_EXTENSTION = ".jpg"

        //Defaults
        const val DEFAULT_USER_IMAGE_LOCATION = "users/anonymous_person.png"

        //Users table
        const val USERS_TABLE_NAME = "users"
        const val USERS_TABLE_SPECIALISATION_ID_COLUMN = "specialisationId"
        const val USERS_NOTIFICATION_TOKEN = "notificationToken"

        //Medics details table
        const val MEDICS_DETAILS_TABLE = "medics_details"

        //Medical specialties table
        const val MEDICAL_SPECIALTIES_TABLE_NAME = "medical_specialties"

        //Appointments table
        const val MEDICAL_APPOINTMENTS_TABLE_NAME = "medical_appointments"
        const val APPOINTMENTS_TABLE_MEDIC_ID = "medicId"
        const val APPOINTMENT_PATIENT_ID = "patientId"

        //Canceled appointments
        const val CANCELED_APPOINTMENTS = "canceled_appointments"

        //Conversations
        const val CONVERSATIONS = "conversations"
        const val CONVERSATION_ID = "id"
        const val FIRST_PARTICIPANT_ID = "firstParticipantId"
        const val SECOND_PARTICIPANT_ID = "secondParticipantId"
        const val CONVERSATION_MESSAGES = "messages"
        const val CONVERSATION_LAST_MESSAGE = "lastMessage"
    }
}