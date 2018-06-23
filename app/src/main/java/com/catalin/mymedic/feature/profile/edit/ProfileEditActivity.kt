package com.catalin.mymedic.feature.profile.edit

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.ProfileEditBinding
import com.catalin.mymedic.R
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/23/2018
 */
class ProfileEditActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ProfileEditViewModel.Factory

    private lateinit var binding: ProfileEditBinding
    private lateinit var viewModel: ProfileEditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileEditViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.profile_edit_activity)
        binding.viewModel = viewModel
        initListeners()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_SELECT && resultCode == Activity.RESULT_OK) {
            data?.let { result ->
                binding.userProfileImage.setImageURI(result.data)
                viewModel.uploadedImage = result.data
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_update_profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.update_profile_item) {
            viewModel.updateProfile()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initListeners() {
        binding.changePhotoButton.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).setType("image/*"), REQUEST_IMAGE_SELECT)
        }
    }

    companion object {
        private const val USER_ID = "userId"
        private const val REQUEST_IMAGE_SELECT = 1

        private val Intent.userId
            get() = this.getStringExtra(USER_ID)

        fun getStartIntent(context: Context, userId: String): Intent = Intent(context, ProfileEditActivity::class.java).putExtra(USER_ID, userId)
    }
}