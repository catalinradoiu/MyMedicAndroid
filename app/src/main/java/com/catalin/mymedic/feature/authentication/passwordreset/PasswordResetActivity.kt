package com.catalin.mymedic.feature.authentication.passwordreset

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.inputmethod.InputMethodManager
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.PasswordResetActivityBinding
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.newLongSnackbar
import com.catalin.mymedic.utils.extension.onPropertyChanged
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/10/2018
 */
class PasswordResetActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: PasswordResetViewModel.PasswordResetViewModelProvider

    private lateinit var viewModel: PasswordResetViewModel
    private lateinit var binding: PasswordResetActivityBinding
    private var operationSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(PasswordResetViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.password_reset_activity)
        binding.viewModel = viewModel
        initListeners()
    }

    private fun initListeners() {
        viewModel.passwordResetResult.onPropertyChanged { value ->
            when (value) {
                is OperationResult.Success -> {
                    dismissKeyboard()
                    showSnackbar(getString(R.string.reset_email_sent))
                    viewModel.clearFields()
                }
                is OperationResult.Error -> {
                    dismissKeyboard()
                    value.message?.let {
                        if (it.contains(EMAIL_NOT_FOUND)) {
                            showSnackbar(getString(R.string.email_not_found))
                        } else {
                            showSnackbar(it)
                        }
                    }
                }
            }
        }

        viewModel.validEmail.onPropertyChanged { value ->
            binding.passwordResetEmailLayout.error = if (value) "" else getString(R.string.invalid_email)
        }
    }

    private fun showSnackbar(message: String) {
        operationSnackbar = operationSnackbar.newLongSnackbar(binding.root, message)
    }

    private fun dismissKeyboard() {
        val view = currentFocus
        if (view != null) {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    companion object {
        private const val EMAIL_NOT_FOUND = "EMAIL_NOT_FOUND"

        fun getStartIntent(context: Context) = Intent(context, PasswordResetActivity::class.java)
    }
}