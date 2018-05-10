package com.catalin.mymedic.feature.chat

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.ChatListFragmentBinding

/**
 * @author catalinradoiu
 * @since 4/26/2018
 */
class ChatListFragment : Fragment() {

    private lateinit var binding: ChatListFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.chat_list_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.title = getString(R.string.chat)
    }
}