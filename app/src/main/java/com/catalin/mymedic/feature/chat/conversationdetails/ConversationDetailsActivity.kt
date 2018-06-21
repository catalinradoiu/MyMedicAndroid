package com.catalin.mymedic.feature.chat.conversationdetails

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.catalin.mymedic.ConversationDetailsBinding
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/20/2018
 */
class ConversationDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ConversationDetailsViewModel.Factory

    private lateinit var binding: ConversationDetailsBinding
    private lateinit var viewModel: ConversationDetailsViewModel
    private lateinit var messagesAdapter: ConversationMessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ConversationDetailsViewModel::class.java).apply {
            conversationId = intent.conversationId
            otherParticipantId = intent.otherParticipantId
            otherParticipantName.set(intent.otherParticipantName)
            otherParticipantImageUrl.set(intent.otherParticipantImageUrl)

        }
        messagesAdapter = ConversationMessagesAdapter(viewModel.getCurrentUserId())
        binding = DataBindingUtil.setContentView(this, R.layout.conversation_details_activity)
        binding.viewModel = viewModel
        binding.messagesRecycler.apply {
            adapter = messagesAdapter
            layoutManager = LinearLayoutManager(this@ConversationDetailsActivity)
        }
        viewModel.initConversation()
        initListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initListeners() {
        viewModel.conversationLiveData.observe(this, Observer {
            it?.let { conversation ->
                messagesAdapter.messages = ArrayList(conversation.messages)
                binding.messagesRecycler.scrollToPosition(messagesAdapter.messages.size - 1)
            }
        })
    }

    companion object {

        private const val CONVERSATION_ID = "conversationId"
        private const val OTHER_PARTICIPANT_ID = "otherParticipantId"
        private const val OTHER_PARTICIPANT_IMAGE_URL = "firstParticipantImageUrl"
        private const val OTHER_PARTICIPANT_NAME = "firstParticipantName"

        // When launching the activity from chat fragment
        fun getStartIntent(
            context: Context,
            conversationId: String,
            firstParticipantId: String,
            firstParticipantImageUrl: String,
            firstParticipantName: String
        ): Intent =
            Intent(context, ConversationDetailsActivity::class.java).putExtra(CONVERSATION_ID, conversationId).putExtra(
                OTHER_PARTICIPANT_ID,
                firstParticipantId
            ).putExtra(OTHER_PARTICIPANT_NAME, firstParticipantName).putExtra(OTHER_PARTICIPANT_IMAGE_URL, firstParticipantImageUrl)

        // When launching the activity by click conversation button in the medics search activity
        fun getStartIntent(context: Context, firstParticipantId: String, firstParticipantImageUrl: String, firstParticipantName: String): Intent =
            Intent(context, ConversationDetailsActivity::class.java).putExtra(OTHER_PARTICIPANT_ID, firstParticipantId).putExtra(
                OTHER_PARTICIPANT_NAME,
                firstParticipantName
            ).putExtra(OTHER_PARTICIPANT_IMAGE_URL, firstParticipantImageUrl)

        private val Intent.conversationId
            get() = this.getStringExtra(CONVERSATION_ID).orEmpty()
        private val Intent.otherParticipantId
            get() = this.getStringExtra(OTHER_PARTICIPANT_ID)
        private val Intent.otherParticipantName
            get() = this.getStringExtra(OTHER_PARTICIPANT_NAME)
        private val Intent.otherParticipantImageUrl
            get() = this.getStringExtra(OTHER_PARTICIPANT_IMAGE_URL)
    }
}