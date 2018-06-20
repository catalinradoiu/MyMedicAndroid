package com.catalin.mymedic.storage.source

import com.catalin.mymedic.data.Conversation
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.google.firebase.database.FirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/20/2018
 */
class ConversationsFirebaseSource @Inject constructor(private val firebaseDatabase: FirebaseDatabase) {

    fun getConversationsForUser(userId: String): Flowable<List<Conversation>> =
        RxFirebaseDatabase.observeValueEvent(firebaseDatabase.reference.child(FirebaseDatabaseConfig.CONVERSATIONS), { snapshot ->
            snapshot.children.mapNotNull { value -> value.getValue(Conversation::class.java) }
                .filter { conversation -> conversation.firstParticipantId == userId || conversation.secondParticipantId == userId }
                .sortedBy { it.lastMessage.sendTime }
        })
}