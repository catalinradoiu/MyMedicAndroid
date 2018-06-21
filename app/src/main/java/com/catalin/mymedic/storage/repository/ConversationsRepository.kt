package com.catalin.mymedic.storage.repository

import com.catalin.mymedic.data.Conversation
import com.catalin.mymedic.data.Message
import com.catalin.mymedic.storage.source.ConversationsFirebaseSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author catalinradoiu
 * @since 6/20/2018
 */
@Singleton
class ConversationsRepository @Inject constructor(private val conversationsFirebaseSource: ConversationsFirebaseSource) {

    fun getConversationsForUser(userId: String) = conversationsFirebaseSource.getConversationsForUser(userId)

    fun getConversationById(conversationId: String) = conversationsFirebaseSource.getConversationById(conversationId)

    fun getConversationForParticipants(firstParticipantId: String, secondParticipantId: String) =
        conversationsFirebaseSource.getConversationForParticipants(firstParticipantId, secondParticipantId)

    fun createConversation(conversation: Conversation) = conversationsFirebaseSource.createConversation(conversation)

    fun createMessage(message: Message, conversationId: String) = conversationsFirebaseSource.createMessage(message, conversationId)
}