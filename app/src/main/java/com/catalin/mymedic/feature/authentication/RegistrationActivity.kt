package com.catalin.mymedic.feature.authentication

import android.app.DatePickerDialog
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.RegistrationActivityBinding
import java.util.*
import javax.inject.Inject

class RegistrationActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: RegistrationViewModel.RegistrationViewModelProvider

    private lateinit var binding: RegistrationActivityBinding

    private lateinit var registrationViewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        registrationViewModel = ViewModelProviders.of(this, viewModelFactory).get(RegistrationViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.registration_activity)
        binding.viewModel = registrationViewModel
        initSpinner()
        initListeners()
        initViews()
    }

    private fun initListeners() {
        registrationViewModel.registrationResult.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                sender?.let {
                    if ((it as ObservableBoolean).get()) {
                        Snackbar.make(binding.birthDateInput, "registration ok", Snackbar.LENGTH_LONG).show()
                    } else {
                        Snackbar.make(binding.birthDateInput, "registration error", Snackbar.LENGTH_LONG).show()
                    }
                }
            }

        })

        binding.birthDateInput.setOnClickListener {
            DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                registrationViewModel.birthDate.set(calendar.timeInMillis)
            }, START_YEAR, START_MONTH, START_DAY).show()
        }
    }

    private fun initViews() {
        binding.birthDateInput.isFocusableInTouchMode = false
    }

    private fun initSpinner() {
        val arrayAdapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = arrayAdapter
    }

    companion object {
        private const val START_YEAR = 1920
        private const val START_MONTH = 1
        private const val START_DAY = 1
    }
}
