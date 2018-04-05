package com.catalin.mymedic.feature.authentication

import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.RegistrationActivityBinding
import com.catalin.mymedic.utils.extension.newLongSnackbar
import com.catalin.mymedic.utils.extension.onPropertyChanged
import javax.inject.Inject

class RegistrationActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: RegistrationViewModel.RegistrationViewModelProvider

    private lateinit var binding: RegistrationActivityBinding

    private lateinit var registrationViewModel: RegistrationViewModel

    private var operationSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        registrationViewModel = ViewModelProviders.of(this, viewModelFactory).get(RegistrationViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.registration_activity)
        binding.viewModel = registrationViewModel
        initListeners()
    }

    override fun onStop() {
        super.onStop()
        operationSnackbar?.dismiss()
    }

    private fun initListeners() {
        registrationViewModel.registrationResult.onPropertyChanged { value ->
            when (value) {
                is RegistrationViewModel.OperationResult.Success -> displaySnackBar(getString(R.string.registration_success_with_confirmation_sent))
                is RegistrationViewModel.OperationResult.Error -> displaySnackBar(value.message)
            }
        }
    }

    private fun displaySnackBar(message: String) {
        operationSnackbar = operationSnackbar.newLongSnackbar(binding.registrationRoot, message)
    }

    companion object {
        private const val START_YEAR = 1920
        private const val START_MONTH = 1
        private const val START_DAY = 1
    }
}
