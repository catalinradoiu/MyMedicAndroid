package com.catalin.mymedic.storage.source

import com.catalin.mymedic.data.Conversation
import com.catalin.mymedic.data.Message
import com.catalin.mymedic.data.fromSnapshot
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.google.firebase.database.FirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author catalinradoiu
 * @since 6/20/2018
 */
@Singleton
class ConversationsFirebaseSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    fun getConversationsForUser(userId: String): Flowable<List<Conversation>> =
        RxFirebaseDatabase.observeValueEvent(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.CONVERSATIONS).orderByChild(FirebaseDatabaseConfig.FIRST_PARTICIPANT_ID).equalTo(
                userId
            ), { snapshot ->
                snapshot.children.mapNotNull { value -> Conversation().fromSnapshot(value) }
            }).flatMap { firstResult ->
            RxFirebaseDatabase.observeValueEvent(
                firebaseDatabase.reference.child(FirebaseDatabaseConfig.CONVERSATIONS).orderByChild(
                    FirebaseDatabaseConfig.SECOND_PARTICIPANT_ID
                ).equalTo(userId), { data ->
                    data.children.mapNotNull { value -> Conversation().fromSnapshot(value) }
                }
            ).map { secondResult ->
                val finalResult = ArrayList(firstResult)
                finalResult.addAll(secondResult)
                finalResult
            }
        }

    fun getConversationById(conversationId: String): Flowable<Conversation> =
        RxFirebaseDatabase.observeValueEvent(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.CONVERSATIONS).orderByChild(FirebaseDatabaseConfig.CONVERSATION_ID).equalTo(
                conversationId
            ),
            { data ->
                data.children.mapNotNull { snapshot ->
                    Conversation().fromSnapshot(snapshot).apply { messages.sortedBy { message -> message.sendTime } }
                }.first()
            }
        )

    fun getConversationForParticipants(firstParticipantId: String, secondParticipantId: String): Flowable<Conversation> =
        RxFirebaseDatabase.observeValueEvent(firebaseDatabase.reference.child(FirebaseDatabaseConfig.CONVERSATIONS), { snapshot ->
            snapshot.children.mapNotNull { value -> Conversation().fromSnapshot(value) }.first { conversation ->
                (conversation.firstParticipantId == firstParticipantId && conversation.secondParticipantId == secondParticipantId)
                        || (conversation.firstParticipantId == secondParticipantId && conversation.secondParticipantId == firstParticipantId)
            }.apply { messages.sortedBy { message -> message.sendTime } }
        })

    fun createConversation(conversation: Conversation): Flowable<Conversation> {
        val conversationId = firebaseDatabase.reference.child(FirebaseDatabaseConfig.CONVERSATIONS).push().key ?: ""
        return RxFirebaseDatabase.setValue(firebaseDatabase.reference.child(FirebaseDatabaseConfig.CONVERSATIONS).child(conversationId), conversation.apply {
            id = conversationId
        })
            .toFlowable<Unit>().flatMap { getConversationById(conversationId) }
    }

    fun createMessage(message: Message, conversationId: String): Completable {
        val messageId =
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.CONVERSATIONS).child(conversationId).child(FirebaseDatabaseConfig.CONVERSATION_MESSAGES)
                .push().key.orEmpty()
        return RxFirebaseDatabase.setValue(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.CONVERSATIONS).child(conversationId).child(FirebaseDatabaseConfig.CONVERSATION_MESSAGES).child(
                messageId
            ),
            message.apply {
                id = messageId
            }
        ).andThen(updateLastMessage(message, conversationId))
    }

    private fun updateLastMessage(message: Message, conversationId: String) =
        RxFirebaseDatabase.setValue(
            firebaseDatabase.reference.child(FirebaseDatabaseConfig.CONVERSATIONS).child(conversationId).child(FirebaseDatabaseConfig.CONVERSATION_LAST_MESSAGE),
            message
        )

}