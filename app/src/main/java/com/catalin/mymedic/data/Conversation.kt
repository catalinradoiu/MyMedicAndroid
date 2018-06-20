package com.catalin.mymedic.data

/**
 * @author catalinradoiu
 * @since 6/19/2018
 */
data class Conversation(
    val id: String,
    val firstParticipantId: String,
    val secondParticipantId: String,
    val firstParticipantName: String,
    val secondParticipantName: String,
    var firstParticipantImageUrl: String,
    val secondParticipantImageUrl: String,
    val messages: List<Message>,
    val lastMessage: Message
) {
    constructor() : this("", "", "", "", "", "", "", ArrayList(), Message())
}