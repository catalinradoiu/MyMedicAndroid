package com.catalin.mymedic.feature.medicalrecord.awaitingappointments

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
 * @since 6/15/2018
 */
class AwaitingAppointmentsViewModel(
    private val appointmentsRepository: MedicalAppointmentsRepository,
    private val preferencesManager: SharedPreferencesManager
) : ViewModel() {

    val awaitingAppointments = MutableLiveData<List<MedicalAppointment>>()
    val state = ObservableField<StateLayout.State>(StateLayout.State.LOADING)
    val operationResult = SingleLiveEvent<OperationResult>()

    private val diposables = CompositeDisposable()
    private val updateDiposables = CompositeDisposable()

    fun initAwaitingAppointments() {
        state.set(StateLayout.State.LOADING)
        diposables.add(
            appointmentsRepository.getAwaitingAppointmentsForMedic(preferencesManager.currentUserId).mainThreadSubscribe(
            Consumer { result ->
                if (result.isEmpty()) {
                    state.set(StateLayout.State.EMPTY)
                } else {
                    state.set(StateLayout.State.NORMAL)
                    awaitingAppointments.value = result
                }
            },
            Consumer {
                state.set(StateLayout.State.ERROR)
            }
        ))
    }

    fun approveAppointment(appointment: MedicalAppointment, appointmentStatus: AppointmentStatus) {
        updateDiposables.add(appointmentsRepository.updateMedicalAppointment(appointment.apply {
            status = appointmentStatus
        }).mainThreadSubscribe(Action {
            operationResult.value = OperationResult.Success()
        }, Consumer {
            operationResult.value = OperationResult.Error(it.localizedMessage)
        }))
    }

    fun cancelAppointment(appointment: MedicalAppointment, appointmentStatus: AppointmentStatus, reason: String) {
        updateDiposables.add(appointmentsRepository.updateMedicalAppointment(appointment.apply {
            status = appointmentStatus
        }).mainThreadSubscribe(
                Action {
                    operationResult.value = OperationResult.Success()
                }, Consumer {
                    operationResult.value = OperationResult.Error(it.localizedMessage)
                }
            ))

        appointmentsRepository.createCancelationReason(
            AppointmentCancelationReason(
                appointment.id,
                reason,
                appointmentStatus
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        diposables.clear()
        updateDiposables.clear()
    }

    class Factory @Inject constructor(
        private val appointmentsRepository: MedicalAppointmentsRepository,
        private val preferencesManager: SharedPreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            AwaitingAppointmentsViewModel(appointmentsRepository, preferencesManager) as T

    }
}