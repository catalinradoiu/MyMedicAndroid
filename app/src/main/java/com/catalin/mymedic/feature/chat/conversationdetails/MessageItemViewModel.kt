package com.catalin.mymedic.feature.chat.conversationdetails

import android.databinding.ObservableField
import android.databinding.ObservableLong

/**
 * @author catalinradoiu
 * @since 6/21/2018
 */
class MessageItemViewModel {

    val messageText = ObservableField<String>("")
    val messageTime = ObservableLong()
}