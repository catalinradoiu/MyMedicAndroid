package com.catalin.mymedic.feature.medicalrecord.medicalhistory

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.feature.shared.StateLayout
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.storage.repository.MedicalAppointmentsRepository
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/26/2018
 */
class MedicalHistoryViewModel(
    private val medicalAppointmentsRepository: MedicalAppointmentsRepository,
    private val preferencesManager: SharedPreferencesManager
) :
    ViewModel() {

    val medicalAppointments = MutableLiveData<List<MedicalAppointment>>()

    val state = ObservableField<StateLayout.State>(StateLayout.State.LOADING)

    fun initPastMedicalAppointments() {
        state.set(StateLayout.State.LOADING)
        medicalAppointmentsRepository.getPastMedicalAppointemnts(preferencesManager.currentUserId, System.currentTimeMillis()).mainThreadSubscribe(
            Consumer {
                if (it.isEmpty()) {
                    state.set(StateLayout.State.EMPTY)
                } else {
                    medicalAppointments.value = it
                    state.set(StateLayout.State.NORMAL)
                }
            }, Consumer {
                state.set(StateLayout.State.ERROR)
            }
        )
    }

    fun getCurrentUserId() = preferencesManager.currentUserId

    class Factory @Inject constructor(
        private val medicalAppointmentsRepository: MedicalAppointmentsRepository,
        private val preferencesManager: SharedPreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = MedicalHistoryViewModel(medicalAppointmentsRepository, preferencesManager) as T

    }
}