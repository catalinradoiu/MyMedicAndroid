package com.catalin.mymedic.data

/**
 * @author catalinradoiu
 * @since 6/10/2018
 */
class AppointmentNotification(var title: String = "", var message: String = "") {

    companion object {
        fun createNotification(data: Map<String, String>): AppointmentNotification = AppointmentNotification(data["title"] ?: "", data["message"] ?: "")
    }
}