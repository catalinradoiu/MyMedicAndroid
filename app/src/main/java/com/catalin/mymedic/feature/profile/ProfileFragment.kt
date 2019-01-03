package com.catalin.mymedic.feature.profile

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.*
import com.bumptech.glide.signature.ObjectKey
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.ProfileBinding
import com.catalin.mymedic.R
import com.catalin.mymedic.feature.profile.edit.ProfileEditActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.profile_edit) {
            context?.let {
                startActivityForResult(ProfileEditActivity.getStartIntent(it, viewModel.getCurrentUserId()), PROFILE_EDIT_REQUEST)
                activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PROFILE_EDIT_REQUEST && resultCode == Activity.RESULT_OK) {
            viewModel.getCurrentUserDetails()
        }
    }

    private fun initListeners() {
        binding.profileStateLayout.setOnErrorTryAgainListener {
            viewModel.getCurrentUserDetails()
        }

        viewModel.profileImage.observe(this, Observer {
            it?.let { imageUrl ->
                GlideApp.with(binding.userProfileImage).load(viewModel.firebaseStorage.reference.child(imageUrl))
                    .signature(ObjectKey(System.currentTimeMillis().toString()))
                    .into(binding.userProfileImage)
            }
        })
    }

    companion object {
        private const val PROFILE_EDIT_REQUEST = 1
    }
}