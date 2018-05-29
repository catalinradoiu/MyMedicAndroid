package com.catalin.mymedic.feature.createappointment

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.catalin.mymedic.storage.repository.MedicalAppointmentsRepository
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/29/2018
 */
class AppointmentCreateViewModel(private val medicalAppointmentsRepository: MedicalAppointmentsRepository) : ViewModel() {

    class Factory @Inject constructor(private val medicalAppointmentsRepository: MedicalAppointmentsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = AppointmentCreateViewModel(medicalAppointmentsRepository) as T
    }
}