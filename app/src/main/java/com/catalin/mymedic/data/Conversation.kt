package com.catalin.mymedic.data

import com.google.firebase.database.DataSnapshot

/**
 * @author catalinradoiu
 * @since 6/19/2018
 */
data class Conversation(
    var id: String,
    var firstParticipantId: String,
    var secondParticipantId: String,
    var firstParticipantName: String,
    var secondParticipantName: String,
    var firstParticipantImageUrl: String,
    var secondParticipantImageUrl: String,
    var messages: List<Message>,
    var lastMessage: Message
) {
    constructor() : this("", "", "", "", "", "", "", ArrayList<Message>(), Message())
}

fun Conversation.fromSnapshot(dataSnapshot: DataSnapshot) =
    apply {
        id = dataSnapshot.child("id").getValue(String::class.java).orEmpty()
        firstParticipantId = dataSnapshot.child("firstParticipantId").getValue(String::class.java).orEmpty()
        firstParticipantName = dataSnapshot.child("firstParticipantName").getValue(String::class.java).orEmpty()
        firstParticipantImageUrl = dataSnapshot.child("firstParticipantImageUrl").getValue(String::class.java).orEmpty()
        secondParticipantId = dataSnapshot.child("secondParticipantId").getValue(String::class.java).orEmpty()
        secondParticipantName = dataSnapshot.child("secondParticipantName").getValue(String::class.java).orEmpty()
        secondParticipantImageUrl = dataSnapshot.child("secondParticipantImageUrl").getValue(String::class.java).orEmpty()
        lastMessage = dataSnapshot.child("lastMessage").getValue(Message::class.java) ?: Message()
        messages = dataSnapshot.child("messages").children.mapNotNull { value -> value.getValue(Message::class.java) }
    }
