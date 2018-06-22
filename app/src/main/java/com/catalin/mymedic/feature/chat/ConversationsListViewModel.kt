package com.catalin.mymedic.feature.chat

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import com.catalin.mymedic.data.Conversation
import com.catalin.mymedic.feature.shared.StateLayout
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.storage.repository.ConversationsRepository
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/20/2018
 */
class ConversationsListViewModel(
    private val conversationsRepository: ConversationsRepository,
    private val preferencesManager: SharedPreferencesManager,
    val firebaseStorage: FirebaseStorage
) :
    ViewModel() {

    val state = ObservableField<StateLayout.State>(StateLayout.State.LOADING)
    var conversationsList = MutableLiveData<List<Conversation>>()


    fun initConversations() {
        state.set(StateLayout.State.LOADING)
        conversationsRepository.getConversationsForUser(preferencesManager.currentUserId).mainThreadSubscribe(Consumer {
            val messages = it.filter { it.lastMessage.text.isNotEmpty() }
            if (messages.isEmpty()) {
                state.set(StateLayout.State.EMPTY)
            } else {
                conversationsList.value = ArrayList(messages)
                state.set(StateLayout.State.NORMAL)
            }
        },
            Consumer {
                if (it is NoSuchElementException) {
                    // no elements found
                    state.set(StateLayout.State.EMPTY)
                } else {
                    state.set(StateLayout.State.ERROR)
                }
            })
    }

    fun getUserId() = preferencesManager.currentUserId

    class Factory @Inject constructor(
        private val conversationsRepository: ConversationsRepository,
        private val preferencesManager: SharedPreferencesManager,
        private val firebaseStorage: FirebaseStorage
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            ConversationsListViewModel(conversationsRepository, preferencesManager, firebaseStorage) as T

    }
}