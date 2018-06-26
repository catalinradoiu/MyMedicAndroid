package com.catalin.mymedic.feature.medicalrecord.futureappointments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import com.catalin.mymedic.data.AppointmentCancelationReason
import com.catalin.mymedic.data.AppointmentStatus
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.feature.shared.StateLayout
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.storage.repository.MedicalAppointmentsRepository
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.SingleLiveEvent
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/17/2018
 */
class FutureAppointmentsViewModel(
    private val medicalAppointmentsRepository: MedicalAppointmentsRepository,
    private val sharedPreferencesManager: SharedPreferencesManager
) : ViewModel() {

    val patientAppointments = MutableLiveData<List<MedicalAppointment>>()
    val state = ObservableField<StateLayout.State>(StateLayout.State.LOADING)

    val appointmentCancelResult = SingleLiveEvent<OperationResult>()

    private val disposables = CompositeDisposable()

    fun initMedicalAppointments() {
        state.set(StateLayout.State.LOADING)
        disposables.add(
            medicalAppointmentsRepository.getFutureMedicalAppointments(sharedPreferencesManager.currentUserId, System.currentTimeMillis())
            .mainThreadSubscribe(Consumer {
                if (it.isEmpty()) {
                    state.set(StateLayout.State.EMPTY)
                } else {
                    patientAppointments.value = it
                    state.set(StateLayout.State.NORMAL)
                }
            }, Consumer {
                state.set(StateLayout.State.ERROR)
            })
        )
    }

    fun cancelAppointment(appointment: MedicalAppointment, cancelationReason: String, cancelType: AppointmentStatus) {
        medicalAppointmentsRepository.updateMedicalAppointment(appointment.apply { status = cancelType }).andThen(
            medicalAppointmentsRepository.createCancelationReason(
                AppointmentCancelationReason(
                    appointment.id,
                    cancelationReason, cancelType
                )
            )
        ).mainThreadSubscribe(
            Action {
                appointmentCancelResult.value = OperationResult.Success()
            }, Consumer {
                appointmentCancelResult.value = OperationResult.Error(it.localizedMessage)
            })
    }

    fun getCurrentUserId() = sharedPreferencesManager.currentUserId

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    class Factory @Inject constructor(
        private val medicalAppointmentsRepository: MedicalAppointmentsRepository,
        private val sharedPreferencesManager: SharedPreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            FutureAppointmentsViewModel(medicalAppointmentsRepository, sharedPreferencesManager) as T
    }
}