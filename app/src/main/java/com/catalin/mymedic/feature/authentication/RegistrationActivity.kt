package com.catalin.mymedic.feature.authentication

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
import javax.inject.Inject

class RegistrationActivity : AppCompatActivity() {

    @Inject
    lateinit var registrationViewModel: RegistrationViewModel

    private lateinit var binding: RegistrationActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.registration_activity)
        binding.viewModel = registrationViewModel
        initSpinner()
    }

    override fun onStart() {
        super.onStart()
        registrationViewModel.registrationResult.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                sender?.let {
                    if ((it as ObservableBoolean).get()) {
                        Snackbar.make(binding.birthDateInput, "registration ok", Snackbar.LENGTH_LONG)
                    } else {
                        Snackbar.make(binding.birthDateInput, "registration error", Snackbar.LENGTH_LONG)
                    }
                }
            }

        })
    }

    private fun initSpinner() {
        val arrayAdapter = ArrayAdapter.createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = arrayAdapter
    }
}
