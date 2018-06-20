package com.catalin.mymedic.feature.chat

import android.databinding.ObservableField
import android.databinding.ObservableLong
import com.google.firebase.storage.FirebaseStorage

/**
 * @author catalinradoiu
 * @since 6/19/2018
 */
class ConversationItemViewModel(val firebaseStorage: FirebaseStorage) {

    var userId: String = ""
    val conversationPersonName = ObservableField<String>("")
    val lastMessageText = ObservableField<String>("")
    val lastMessageTime = ObservableLong(0)
}