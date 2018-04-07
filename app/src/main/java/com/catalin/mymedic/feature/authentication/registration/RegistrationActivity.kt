package com.catalin.mymedic.feature.authentication.registration

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.RegistrationActivityBinding
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.newLongSnackbar
import com.catalin.mymedic.utils.extension.onPropertyChanged
import javax.inject.Inject

class RegistrationActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: RegistrationViewModel.RegistrationViewModelProvider

    private lateinit var binding: RegistrationActivityBinding
    private lateinit var viewModel: RegistrationViewModel

    private var operationSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(RegistrationViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.registration_activity)
        binding.viewModel = viewModel
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_24dp)
        initListeners()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
    }

    override fun onStop() {
        super.onStop()
        operationSnackbar?.dismiss()
    }

    private fun initListeners() {
        viewModel.registrationResult.onPropertyChanged { value ->
            when (value) {
                is OperationResult.Success -> displaySnackBar(getString(R.string.registration_success_with_confirmation_sent))
                is OperationResult.Error -> displaySnackBar(value.message)
            }
        }
        viewModel.passwordsMatch.onPropertyChanged { value ->
            binding.passwordConfirmationLayout.error =
                    if (value) "" else getString(R.string.registration_passwords_not_match)
        }
        viewModel.validPassword.onPropertyChanged { value ->
            binding.passwordLayout.error = if (value) "" else getString(R.string.invalid_password)

        }
        viewModel.validEmail.onPropertyChanged { value ->
            binding.emailLayout.error = if (value) "" else getString(R.string.invalid_email)
        }
    }

    private fun displaySnackBar(message: String) {
        operationSnackbar = operationSnackbar.newLongSnackbar(binding.registrationRoot, message)
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, RegistrationActivity::class.java)
    }
}
