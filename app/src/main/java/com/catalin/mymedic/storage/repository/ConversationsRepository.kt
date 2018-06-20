package com.catalin.mymedic.storage.repository

import com.catalin.mymedic.storage.source.ConversationsFirebaseSource
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/20/2018
 */
class ConversationsRepository @Inject constructor(private val conversationsFirebaseSource: ConversationsFirebaseSource) {

    fun getConversationsForUser(userId: String) = conversationsFirebaseSource.getConversationsForUser(userId)
}