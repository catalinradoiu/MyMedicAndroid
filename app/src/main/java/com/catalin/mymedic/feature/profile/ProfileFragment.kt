package com.catalin.mymedic.feature.profile

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.ProfileBinding
import com.catalin.mymedic.R
import com.catalin.mymedic.utils.GlideApp
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 4/26/2018
 */
class ProfileFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ProfileViewModel.Factory

    private lateinit var binding: ProfileBinding
    private lateinit var viewModel: ProfileViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MyMedicApplication).applicationComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel::class.java)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            title = getString(R.string.profile)
            elevation = resources.getDimension(R.dimen.standard_elevation)
        }
        initListeners()
        viewModel.getCurrentUserDetails()
    }

    private fun initListeners() {
        viewModel.profileImage.observe(this, Observer {
            it?.let { imageUrl ->
                GlideApp.with(binding.userProfileImage).load(viewModel.firebaseStorage.reference.child(imageUrl)).into(binding.userProfileImage)
            }
        })
    }
}