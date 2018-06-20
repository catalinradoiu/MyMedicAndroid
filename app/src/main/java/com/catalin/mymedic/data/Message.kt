package com.catalin.mymedic.data

/**
 * @author catalinradoiu
 * @since 6/19/2018
 */
data class Message(val id: String, val text: String, val senderId: String, val receiverId: String, val sendTime: String) {

    constructor() : this("", "", "", "", "")
}