package com.catalin.mymedic.feature.createappointment

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import android.databinding.ObservableLong
import com.catalin.mymedic.data.AppointmentStatus
import com.catalin.mymedic.data.AvailableAppointments
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.storage.repository.MedicalAppointmentsRepository
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.SingleLiveEvent
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import com.catalin.mymedic.utils.extension.setToDayStart
import com.wdullaer.materialdatetimepicker.time.Timepoint
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * @author catalinradoiu
 * @since 5/29/2018
 */
class AppointmentCreateViewModel(
    private val medicalAppointmentsRepository: MedicalAppointmentsRepository,
    private val preferencesManager: SharedPreferencesManager
) : ViewModel() {

    val medicId = ObservableField<String>("")
    val patientId = ObservableField<String>("")
    val appointmentTime = ObservableLong(0)
    val appointmentDetails = ObservableField<String>("")
    val medicName = ObservableField<String>("")
    val medicalSpecialty = ObservableField<String>("")
    val validDate = SingleLiveEvent<Boolean>()
    val checkOffline = SingleLiveEvent<Boolean>()
    val operationResult = SingleLiveEvent<OperationResult>()

    var availableAppointmentsDetails = AvailableAppointments()

    private val disposables = CompositeDisposable()

    fun initFreeAppointmentsTimeData(medicId: String) {
        medicalAppointmentsRepository.getAvailableAppointmentsTime(medicId).mainThreadSubscribe(Consumer { result ->
            availableAppointmentsDetails = result
        }, Consumer {
        })
    }

    fun createNewAppointment() {
        if (appointmentTime.get() < System.currentTimeMillis()) {
            validDate.value = false
        } else {
            val unavailableDayTime = addUnavailableTimeToDay(appointmentTime.get())
            validDate.value = true
            checkOffline.value = true
            disposables.add(
                medicalAppointmentsRepository.createAppointment(
                    MedicalAppointment(
                        "",
                        preferencesManager.currentUserName,
                        appointmentTime.get(),
                        preferencesManager.currentUserName,
                        medicName.get().orEmpty(),
                        medicalSpecialty.get().orEmpty(),
                        patientId.get().orEmpty(),
                        medicId.get().orEmpty(),
                        appointmentDetails.get().orEmpty(),
                        AppointmentStatus.AWAITING
                    ), unavailableDayTime
                ).mainThreadSubscribe(Action {
                    operationResult.value = OperationResult.Success()
                }, Consumer {
                    operationResult.value = OperationResult.Error(it.localizedMessage)
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

    private fun addUnavailableTimeToDay(dayTime: Long): Pair<Long, Timepoint> {
        val dayCalendar = Calendar.getInstance().apply {
            timeInMillis = dayTime
        }
        val unavailableTimepoint = Timepoint(dayCalendar.get(Calendar.HOUR_OF_DAY), dayCalendar.get(Calendar.MINUTE))

        //Reset de calendar to the beginning of the day and get the time
        val dayStart = dayCalendar.setToDayStart().timeInMillis

        @Suppress("ReplacePutWithAssignment")
        availableAppointmentsDetails.unselectableTimesForDays[dayStart]?.add(unavailableTimepoint) ?: availableAppointmentsDetails.unselectableTimesForDays.put(
            dayStart,
            ArrayList<Timepoint>().apply { add(unavailableTimepoint) })

        return Pair(dayStart, unavailableTimepoint)
    }

    class Factory @Inject constructor(
        private val medicalAppointmentsRepository: MedicalAppointmentsRepository,
        private val sharedPreferencesManager: SharedPreferencesManager
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            AppointmentCreateViewModel(medicalAppointmentsRepository, sharedPreferencesManager) as T
    }
}