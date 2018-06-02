package com.catalin.mymedic.feature.createappointment

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.databinding.ObservableLong
import android.util.Log
import com.catalin.mymedic.data.AppointmentStatus
import com.catalin.mymedic.data.AvailableAppointments
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.storage.repository.MedicalAppointmentsRepository
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.SingleLiveEvent
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import com.wdullaer.materialdatetimepicker.time.Timepoint
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.*
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/29/2018
 */
class AppointmentCreateViewModel(private val medicalAppointmentsRepository: MedicalAppointmentsRepository) : ViewModel() {

    val medicId = ObservableField<String>("")
    val patientId = ObservableField<String>("")
    val appointmentTime = ObservableLong(0)
    val appointmentDetails = ObservableField<String>("")
    val validDate = SingleLiveEvent<Boolean>()
    val operationResult = SingleLiveEvent<OperationResult>()

    var availableAppointmentsDetails = AvailableAppointments()

    private val disposables = CompositeDisposable()

    fun initFreeAppointmentsTimeData(medicId: String) {
        medicalAppointmentsRepository.getAvailableAppointmentsTime(medicId).mainThreadSubscribe(Consumer { result ->
            availableAppointmentsDetails = result
            Log.d("operationStatus", "finished")
        }, Consumer {
            Log.d("operationStatus", "error : " + it.localizedMessage)
        })
    }

    fun createNewAppointment() {
        if (appointmentTime.get() < System.currentTimeMillis()) {
            validDate.value = false
        } else {
            validDate.value = true
            disposables.add(
                medicalAppointmentsRepository.createAppointment(
                    MedicalAppointment(
                        "",
                        appointmentTime.get(),
                        patientId.get().orEmpty(),
                        medicId.get().orEmpty(),
                        appointmentDetails.get().orEmpty(),
                        AppointmentStatus.AWAITING
                    )
                ).mainThreadSubscribe(Action {
                    operationResult.value = OperationResult.Success()
                    Log.d("operationStatus", "finished")
                }, Consumer {
                    operationResult.value = OperationResult.Error(it.localizedMessage)
                    Log.d("operationStatus", "error : " + it.localizedMessage)
                })
            )
        }
    }

    fun getSelectableTimes(dayTime: Long): ArrayList<Timepoint>? {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = dayTime
        }
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> availableAppointmentsDetails.defaultSelectableTimeForWeek[Calendar.MONDAY]
            Calendar.TUESDAY -> availableAppointmentsDetails.defaultSelectableTimeForWeek[Calendar.TUESDAY]
            Calendar.WEDNESDAY -> availableAppointmentsDetails.defaultSelectableTimeForWeek[Calendar.WEDNESDAY]
            Calendar.THURSDAY -> availableAppointmentsDetails.defaultSelectableTimeForWeek[Calendar.THURSDAY]
            Calendar.FRIDAY -> availableAppointmentsDetails.defaultSelectableTimeForWeek[Calendar.FRIDAY]
            else -> null
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    class Factory @Inject constructor(private val medicalAppointmentsRepository: MedicalAppointmentsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T = AppointmentCreateViewModel(medicalAppointmentsRepository) as T
    }
}