package com.catalin.mymedic.feature.profile.edit

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.bumptech.glide.signature.ObjectKey
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.ProfileEditBinding
import com.catalin.mymedic.R
import com.catalin.mymedic.data.Gender
import com.catalin.mymedic.utils.GlideApp
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.newLongSnackbar
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

    private var operationSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileEditViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.profile_edit_activity)
        binding.viewModel = viewModel
        initListeners()
        viewModel.initUserProfile(intent.userId)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        binding.genderSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        supportActionBar?.title = getString(R.string.edit_profile)
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
        binding.profileEditStateLayout.setOnErrorTryAgainListener {
            viewModel.initUserProfile(intent.userId)
        }

        binding.changePhotoButton.setOnClickListener {
            startActivityForResult(Intent(Intent.ACTION_PICK).setType("image/*"), REQUEST_IMAGE_SELECT)
        }

        binding.birthDateInput.setOnClickListener {
            displayDatePickerDialog()
        }

        binding.genderSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    NOT_SELECTED_GENDER_POSITION -> viewModel.user.gender = Gender.NOT_COMPLETED
                    MALE_GENDER_POSITION -> viewModel.user.gender = Gender.MALE
                    FEMALE_GENDER_POSITION -> viewModel.user.gender = Gender.FEMALE
                }
            }

        }

        viewModel.validName.observe(this, Observer {
            it?.let { valid ->
                binding.nameLayout.error = if (valid) "" else getString(R.string.invalid_name)
            }
        })

        viewModel.validOldPassword.observe(this, Observer {
            it?.let { valid ->
                binding.oldPasswordLayout.error = if (valid) "" else getString(R.string.invalid_password)
            }
        })

        viewModel.validNewPassword.observe(this, Observer {
            it?.let { valid ->
                binding.newPasswordLayout.error = if (valid) "" else getString(R.string.invalid_password)
            }
        })

        viewModel.passwordsMatch.observe(this, Observer {
            it?.let { valid ->
                binding.newPasswordConfirmationLayout.error = if (valid) "" else getString(R.string.registration_passwords_not_match)
            }
        })

        viewModel.profileUpdateResult.observe(this, Observer {
            when (it) {
                is OperationResult.Success -> {
                    setResult(Activity.RESULT_OK)
                    finish()
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                }
                is OperationResult.Error -> displaySnackbar(it.message.orEmpty())
            }
        })

        viewModel.passwordChangeResult.observe(this, Observer { result ->
            when (result) {
                is OperationResult.Success -> displaySnackbar(getString(R.string.password_update_success))
                is OperationResult.Error -> displaySnackbar(result.message.orEmpty())
            }
        })

        viewModel.userGender.observe(this, Observer {
            it?.let { gender ->
                when (gender) {
                    Gender.MALE -> binding.genderSpinner.setSelection(MALE_GENDER_POSITION)
                    Gender.FEMALE -> binding.genderSpinner.setSelection(FEMALE_GENDER_POSITION)
                    else -> binding.genderSpinner.setSelection(NOT_SELECTED_GENDER_POSITION)
                }
            }
        })

        viewModel.userImage.observe(this, Observer { image ->
            image?.let {
                GlideApp.with(binding.userProfileImage).load(viewModel.firebaseStorage.reference.child(it))
                    .signature(ObjectKey(System.currentTimeMillis().toString())).into(binding.userProfileImage)
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

    private fun displaySnackbar(message: String) {
        operationSnackbar.newLongSnackbar(binding.root, message)
    }

    companion object {
        private const val USER_ID = "userId"
        private const val DATE_PICKER_DIALOG_TAG = "datePickerDialog"
        private const val REQUEST_IMAGE_SELECT = 1
        private const val MIN_YEAR = 1910

        private const val NOT_SELECTED_GENDER_POSITION = 2
        private const val MALE_GENDER_POSITION = 0
        private const val FEMALE_GENDER_POSITION = 1

        private val Intent.userId
            get() = this.getStringExtra(USER_ID)

        fun getStartIntent(context: Context, userId: String): Intent = Intent(context, ProfileEditActivity::class.java).putExtra(USER_ID, userId)
    }
}