package com.catalin.mymedic.data

/**
 * @author catalinradoiu
 * @since 6/19/2018
 */
data class Message(var id: String, val text: String, val senderId: String, val receiverId: String, val sendTime: Long) {

    constructor() : this("", "", "", "", 0)
}