package com.catalin.mymedic.feature.chat

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.data.Conversation
import com.catalin.mymedic.databinding.ChatListFragmentBinding
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 4/26/2018
 */
class ConversationsListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ConversationsListViewModel.Factory

    private lateinit var binding: ChatListFragmentBinding
    private lateinit var viewModel: ConversationsListViewModel
    lateinit var conversationsAdapter: ConversationsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MyMedicApplication).applicationComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_list_fragment, container, false)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ConversationsListViewModel::class.java)
        binding.viewModel = viewModel
        conversationsAdapter = ConversationsAdapter(viewModel.getUserId(), viewModel.firebaseStorage)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            title = getString(R.string.chat)
            elevation = resources.getDimension(R.dimen.standard_elevation)
        }
        binding.conversationsList.apply {
            adapter = conversationsAdapter
            layoutManager = LinearLayoutManager(context)
        }
        viewModel.initConversations()
        initListeners()
    }

    private fun initListeners() {
        binding.conversationsStateLayout.setOnErrorTryAgainListener {
            viewModel.initConversations()
        }

        viewModel.conversationsList.observe(this, Observer { conversations ->
            conversationsAdapter.conversations = conversations as ArrayList<Conversation>
        })
    }
}