package com.catalin.mymedic.feature.appointmentdetails

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import com.catalin.mymedic.data.AppointmentCancelationReason
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.feature.shared.StateLayout
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.storage.repository.MedicalAppointmentsRepository
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/26/2018
 */
class AppointmentDetailsViewModel(private val appointmentsRepository: MedicalAppointmentsRepository, private val preferencesManager: SharedPreferencesManager) :
    ViewModel() {

    val appointment = ObservableField<MedicalAppointment>(MedicalAppointment())
    val appointmentCancelationReason = ObservableField<AppointmentCancelationReason>()
    val canceledAppointment = ObservableBoolean(false)
    val state = ObservableField<StateLayout.State>(StateLayout.State.LOADING)

    private val disposables = CompositeDisposable()

    fun getAppointmentDetails(appointmentId: String) {
        state.set(StateLayout.State.LOADING)
        disposables.add(appointmentsRepository.getAppointmentDetails(appointmentId).mainThreadSubscribe(Consumer {
            appointment.set(it.first)
            it.second?.let { cancelationReason ->
                appointmentCancelationReason.set(cancelationReason)
                canceledAppointment.set(true)
            }
            state.set(StateLayout.State.NORMAL)
        }, Consumer {
            if (it is NoSuchElementException) {
                state.set(StateLayout.State.EMPTY)
            } else {
                state.set(StateLayout.State.ERROR)
            }
        }))
    }

    fun getCurrentUserId() = preferencesManager.currentUserId

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    class Factory @Inject constructor(
        private val appointmentsRepository: MedicalAppointmentsRepository,
        private val preferencesManager: SharedPreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = AppointmentDetailsViewModel(appointmentsRepository, preferencesManager) as T

    }
}