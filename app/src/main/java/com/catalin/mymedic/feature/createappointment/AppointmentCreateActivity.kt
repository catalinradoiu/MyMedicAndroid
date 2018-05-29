package com.catalin.mymedic.feature.createappointment

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.AppointmentCreateActivityBinding
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/29/2018
 */
class AppointmentCreateActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: AppointmentCreateViewModel.Factory

    private lateinit var binding: AppointmentCreateActivityBinding
    private lateinit var viewModel: AppointmentCreateViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.appointment_create_activity)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AppointmentCreateViewModel::class.java)
        binding.viewModel = viewModel

        binding.medicNameInput.setText(intent.getStringExtra(MEDIC_NAME))
        binding.sectionNameInput.setText(intent.getStringExtra(MEDICAL_SPECIALTY_NAME))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val MEDIC_NAME = "medicName"
        private const val MEDIC_ID = "medicId"
        private const val MEDICAL_SPECIALTY_NAME = "medicalSpecialtyName"

        fun getStartIntent(context: Context, medicName: String, medicalSpecialty: String, medicId: Int): Intent =
            Intent(context, AppointmentCreateActivity::class.java)
                .putExtra(MEDIC_NAME, medicName).putExtra(MEDIC_ID, medicId).putExtra(MEDICAL_SPECIALTY_NAME, medicalSpecialty)
    }
}