package com.catalin.mymedic.feature.authentication.login

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.style.UnderlineSpan
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.LoginActivityBinding
import com.catalin.mymedic.feature.authentication.passwordreset.PasswordResetActivity
import com.catalin.mymedic.feature.authentication.registration.RegistrationActivity
import com.catalin.mymedic.feature.home.HomeActivity
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.dismissIfVisible
import com.catalin.mymedic.utils.extension.newLongSnackbar
import com.catalin.mymedic.utils.extension.onPropertyChanged
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 4/6/2018
 */
class LoginActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: LoginViewModel.LoginViewModelProvider

    private lateinit var binding: LoginActivityBinding
    private lateinit var viewModel: LoginViewModel

    private var operationSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.login_activity)
        binding.viewModel = viewModel

        val forgotPasswordText = SpannableString(getString(R.string.login_forgot_password))
        forgotPasswordText.setSpan(UnderlineSpan(), 0, forgotPasswordText.length, 0)
        binding.forgotPasswordText.text = forgotPasswordText
        initListeners()
    }

    override fun onStop() {
        super.onStop()
        operationSnackbar?.dismissIfVisible()
    }

    private fun initListeners() {
        viewModel.loginResult.onPropertyChanged { value ->
            when (value) {
                is OperationResult.Error -> {
                    value.message?.let {
                        displaySnackBar(it)
                    }
                }
                is OperationResult.Success -> {
                    startActivity(HomeActivity.getStartIntent(this))
                    finish()
                }
            }
        }
        viewModel.validPassword.onPropertyChanged { value ->
            binding.loginPasswordLayout.error = if (value) "" else getString(R.string.invalid_password)
        }
        viewModel.validEmail.onPropertyChanged { value ->
            binding.loginEmailLayout.error = if (value) "" else getString(R.string.invalid_email)
        }

        binding.signUpButton.setOnClickListener {
            startActivity(RegistrationActivity.getStartIntent(this))
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        binding.forgotPasswordText.setOnClickListener {
            startActivity(PasswordResetActivity.getStartIntent(this))
        }
    }

    private fun displaySnackBar(message: String) {
        operationSnackbar.newLongSnackbar(binding.root, message)
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}