package com.catalin.mymedic.data

/**
 *  Class for holding the user data
 */
data class User(
    var id: String,
    var displayName: String,
    var email: String,
    var birthDate: Long,
    var gender: Gender,
    var specialisationId: Int,
    var imageUrl: String,
    var notificationToken: String = ""
) {
    /*
        Empty constructor needed for parsing the firebase json to an actual User object
        DataSnapshot.getValue(User::class.java)
     */
    constructor() : this("", "", "", 0, Gender.NOT_COMPLETED, 0, "")
}

enum class Gender {
    MALE, FEMALE, NOT_COMPLETED
}

fun User.toMap() = HashMap<String, Any>().apply {
    put("id", id)
    put("displayName", displayName)
    put("email", email)
    put("birthDate", birthDate)
    put("gender", gender)
    put("specialisationId", specialisationId)
    put("imageUrl", imageUrl)
    put("notificationToken", notificationToken)
}