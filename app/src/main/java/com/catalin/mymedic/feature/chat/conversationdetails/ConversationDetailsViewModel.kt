package com.catalin.mymedic.feature.chat.conversationdetails

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import com.catalin.mymedic.data.Conversation
import com.catalin.mymedic.data.Message
import com.catalin.mymedic.feature.shared.StateLayout
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.storage.repository.ConversationsRepository
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.SingleLiveEvent
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/20/2018
 */
class ConversationDetailsViewModel(
    private val conversationsRepository: ConversationsRepository,
    val firebaseStorage: FirebaseStorage,
    private val preferencesManager: SharedPreferencesManager
) :
    ViewModel() {

    val state = ObservableField<StateLayout.State>(StateLayout.State.LOADING)
    val conversationLiveData = MutableLiveData<Conversation>()

    var conversationId = ""
    var otherParticipantId = ""
    val otherParticipantImageUrl = ObservableField<String>("")
    val otherParticipantName = ObservableField<String>("")

    val messageText = ObservableField<String>("")
    val operationError = SingleLiveEvent<OperationResult>()

    private val disposables = CompositeDisposable()
    private val messageDisposables = CompositeDisposable()

    fun initConversation() {
        state.set(StateLayout.State.LOADING)
        disposables.add(
            getConversation().mainThreadSubscribe(Consumer {
                conversationId = it.id
                if (it.messages.isEmpty()) {
                    state.set(StateLayout.State.EMPTY)
                } else {
                    state.set(StateLayout.State.NORMAL)
                    conversationLiveData.value = it
                }
            },
                Consumer {
                    state.set(StateLayout.State.ERROR)
                })
        )
    }

    fun getCurrentUserId() = preferencesManager.currentUserId

    fun sendMessage() {
        val text = messageText.get().orEmpty()
        if (text.isNotEmpty()) {
            messageText.set("")
            messageDisposables.add(
                conversationsRepository.createMessage(
                    Message("", text, preferencesManager.currentUserId, otherParticipantId, System.currentTimeMillis()),
                    conversationId
                ).mainThreadSubscribe(Action { }, Consumer {
                    operationError.value = OperationResult.Error(it.localizedMessage)
                })
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
        messageDisposables.clear()
    }

    private fun getConversation() =
        if (conversationId.isEmpty()) {
            conversationsRepository.getConversationForParticipants(preferencesManager.currentUserId, otherParticipantId)
                .onErrorResumeNext(Function<Throwable, Flowable<Conversation>> { it ->
                    if (it is NoSuchElementException) {
                        val currentUserAvatar = preferencesManager.currentUserAvatar
                        val otherParticipantImageUrl = otherParticipantImageUrl.get().orEmpty()
//                        state.set(StateLayout.State.EMPTY)
                        conversationsRepository.createConversation(Conversation().apply {
                            firstParticipantId = preferencesManager.currentUserId
                            firstParticipantName = preferencesManager.currentUserName
                            firstParticipantImageUrl =
                                    if (currentUserAvatar.isNotEmpty()) currentUserAvatar else FirebaseDatabaseConfig.DEFAULT_USER_IMAGE_LOCATION
                            secondParticipantId = otherParticipantId
                            secondParticipantName = otherParticipantName.get().orEmpty()
                            secondParticipantImageUrl =
                                    if (otherParticipantImageUrl.isNotEmpty()) otherParticipantImageUrl else FirebaseDatabaseConfig.DEFAULT_USER_IMAGE_LOCATION
                        })
                    } else {
                        throw it
                    }
                })
        } else {
            conversationsRepository.getConversationById(conversationId)
        }

    class Factory @Inject constructor(
        private val conversationsRepository: ConversationsRepository,
        private val firebaseStorage: FirebaseStorage,
        private val preferencesManager: SharedPreferencesManager
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>) =
            ConversationDetailsViewModel(conversationsRepository, firebaseStorage, preferencesManager) as T

    }
}

