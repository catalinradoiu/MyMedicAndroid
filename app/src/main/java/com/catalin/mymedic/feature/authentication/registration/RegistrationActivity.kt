package com.catalin.mymedic.feature.authentication.registration

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.WindowManager
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.RegistrationActivityBinding
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.dismissIfVisible
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
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        val alreadyRegisteredContent = SpannableString(getString(R.string.already_registered))
        alreadyRegisteredContent.setSpan(UnderlineSpan(), 0, alreadyRegisteredContent.length, 0)
        binding.alreadyRegisteredText.text = alreadyRegisteredContent
        initListeners()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    override fun onStop() {
        super.onStop()
        operationSnackbar?.dismissIfVisible()
    }

    private fun initListeners() {
        viewModel.registrationResult.onPropertyChanged { value ->
            when (value) {
                is OperationResult.Success -> {
                    viewModel.clearFields()
                    displaySnackBar(getString(R.string.registration_success_with_confirmation_sent))
                }
                is OperationResult.Error -> {
                    value.message?.let {
                        displaySnackBar(it)
                    }
                }
            }
        }
        viewModel.passwordsMatch.onPropertyChanged { value ->
            binding.passwordConfirmationLayout.error = if (value) "" else getString(R.string.registration_passwords_not_match)
        }
        viewModel.validPassword.onPropertyChanged { value ->
            binding.passwordLayout.error = if (value) "" else getString(R.string.invalid_password)
        }
        viewModel.validEmail.onPropertyChanged { value ->
            binding.emailLayout.error = if (value) "" else getString(R.string.invalid_email)
        }

        viewModel.validName.observe(this, Observer { valid ->
            valid?.let {
                binding.nameLayout.error = if (it) "" else getString(R.string.invalid_name)
            }
        })

        binding.alreadyRegisteredText.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }

    private fun displaySnackBar(message: String) {
        operationSnackbar = operationSnackbar.newLongSnackbar(binding.registrationRoot, message)
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, RegistrationActivity::class.java)
    }
}
