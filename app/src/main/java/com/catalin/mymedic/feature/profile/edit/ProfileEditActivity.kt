package com.catalin.mymedic.feature.profile.edit

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.ProfileEditBinding
import com.catalin.mymedic.R
import com.catalin.mymedic.utils.GlideApp
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*
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
        viewModel.initUserProfile(intent.userId)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
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
            viewModel.updateProfile(intent.userId)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        return true
    }

    private fun initListeners() {
        binding.changePhotoButton.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).setType("image/*"), REQUEST_IMAGE_SELECT)
        }

        binding.birthDateInput.setOnClickListener {
            displayDatePickerDialog()
        }

        viewModel.userImage.observe(this, Observer { image ->
            image?.let {
                GlideApp.with(binding.userProfileImage).load(viewModel.firebaseStorage.reference.child(it)).into(binding.userProfileImage)
            }
        })
    }

    private fun displayDatePickerDialog() {
        val currentCalendar = Calendar.getInstance()
        DatePickerDialog.newInstance({ _, year, month, day ->
            viewModel.userBirthDate.set(Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, day)
            }.timeInMillis)
        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH)).apply {
            maxDate = currentCalendar
            minDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, MIN_YEAR)
            }
        }.show(fragmentManager, DATE_PICKER_DIALOG_TAG)
    }

    companion object {
        private const val USER_ID = "userId"
        private const val DATE_PICKER_DIALOG_TAG = "datePickerDialog"
        private const val REQUEST_IMAGE_SELECT = 1
        private const val MIN_YEAR = 1910

        private val Intent.userId
            get() = this.getStringExtra(USER_ID)

        fun getStartIntent(context: Context, userId: String): Intent = Intent(context, ProfileEditActivity::class.java).putExtra(USER_ID, userId)
    }
}