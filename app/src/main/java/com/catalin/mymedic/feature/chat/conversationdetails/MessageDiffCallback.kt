package com.catalin.mymedic.feature.chat.conversationdetails

import android.support.v7.util.DiffUtil
import com.catalin.mymedic.data.Message


/**
 * @author catalinradoiu
 * @since 6/21/2018
 */
class MessageDiffCallback(private var newMessages: List<Message>, private var oldMessages: List<Message>) : DiffUtil.Callback() {

    override fun getOldListSize() = oldMessages.size

    override fun getNewListSize() = newMessages.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldMessages[oldItemPosition].id === newMessages[newItemPosition].id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldMessages[oldItemPosition].id == newMessages[newItemPosition].id
}