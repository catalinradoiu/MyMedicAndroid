package com.catalin.mymedic.feature.chat.conversationdetails

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.catalin.mymedic.OtherMessageBinding
import com.catalin.mymedic.OwnMessageBinding
import com.catalin.mymedic.R
import com.catalin.mymedic.data.Message

/**
 * @author catalinradoiu
 * @since 6/21/2018
 */
class ConversationMessagesAdapter(private val userId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var messages = ArrayList<Message>()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(MessageDiffCallback(value, field))
            field.clear()
            field.addAll(value)
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.own_message_item -> OwnMessageItem(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false),
            MessageItemViewModel()
        )
        else -> OtherMessageItem(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.other_message_item, parent, false),
            MessageItemViewModel()
        )
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is OwnMessageItem -> holder.bind(messages[position])
            is OtherMessageItem -> holder.bind(messages[position])
        }
    }

    override fun getItemViewType(position: Int) = if (messages[position].senderId == userId) R.layout.own_message_item else R.layout.other_message_item

    class OwnMessageItem(binding: OwnMessageBinding, private val viewModel: MessageItemViewModel) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = viewModel
        }

        fun bind(message: Message) {
            viewModel.apply {
                messageText.set(message.text)
                messageTime.set(message.sendTime)
            }
        }
    }

    class OtherMessageItem(binding: OtherMessageBinding, private val viewModel: MessageItemViewModel) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = viewModel
        }

        fun bind(message: Message) {
            viewModel.apply {
                messageText.set(message.text)
                messageTime.set(message.sendTime)
            }
        }
    }
}