package com.catalin.mymedic.feature.chat

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.catalin.mymedic.ConversationItemBinding
import com.catalin.mymedic.R
import com.catalin.mymedic.data.Conversation
import com.catalin.mymedic.utils.GlideApp
import com.google.firebase.storage.FirebaseStorage

/**
 * @author catalinradoiu
 * @since 6/19/2018
 */
class ConversationsAdapter(private var userId: String = "", private val firebaseStorage: FirebaseStorage) :
    RecyclerView.Adapter<ConversationsAdapter.ConversationItemHolder>() {

    var conversations = ArrayList<Conversation>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var onConversationClickListener: OnConversationClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ConversationItemHolder(
        DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.conversation_item, parent, false),
        ConversationItemViewModel(firebaseStorage).apply { userId = this@ConversationsAdapter.userId },
        onConversationClickListener
    )

    override fun onBindViewHolder(holder: ConversationItemHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount() = conversations.size

    fun setOnConversationClickListener(onConversationClickListener: OnConversationClickListener) {
        this.onConversationClickListener = onConversationClickListener
    }


    class ConversationItemHolder(
        private val binding: ConversationItemBinding,
        private val viewModel: ConversationItemViewModel,
        onConversationClickListener: OnConversationClickListener?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = viewModel
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onConversationClickListener?.onClick(position)
                }
            }
        }

        fun bind(conversation: Conversation) {
            viewModel.apply {
                conversationPersonName.set(if (viewModel.userId == conversation.firstParticipantId) conversation.secondParticipantName else conversation.firstParticipantName)
                lastMessageText.set(conversation.lastMessage.text)
//                lastMessageTime.set(conversation.lastMessage.sendTime)
            }
            GlideApp.with(binding.conversationPersonImage)
                .load(viewModel.firebaseStorage.reference.child(if (viewModel.userId == conversation.firstParticipantId) conversation.firstParticipantImageUrl else conversation.firstParticipantImageUrl))
                .into(binding.conversationPersonImage)
        }
    }

    interface OnConversationClickListener {
        fun onClick(position: Int)
    }
}