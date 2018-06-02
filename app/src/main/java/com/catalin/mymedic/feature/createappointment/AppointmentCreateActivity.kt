package com.catalin.mymedic.feature.createappointment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.AppointmentCreateActivityBinding
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.utils.NetworkManager
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.dismissIfVisible
import com.catalin.mymedic.utils.extension.newLongSnackbar
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.util.*
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/29/2018
 */
class AppointmentCreateActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: AppointmentCreateViewModel.Factory

    @Inject
    lateinit var preferencesManager: SharedPreferencesManager

    private lateinit var binding: AppointmentCreateActivityBinding
    private lateinit var viewModel: AppointmentCreateViewModel

    private var operationSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.appointment_create_activity)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AppointmentCreateViewModel::class.java).apply {
            medicId.set(intent.getStringExtra(MEDIC_ID))
            patientId.set(preferencesManager.currentUserId)
            initFreeAppointmentsTimeData(intent.getStringExtra(MEDIC_ID))
        }
        binding.viewModel = viewModel

        supportActionBar?.let {
            title = getString(R.string.request_appointment)
        }

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        binding.medicNameInput.setText(intent.getStringExtra(MEDIC_NAME))
        binding.sectionNameInput.setText(intent.getStringExtra(MEDICAL_SPECIALTY_NAME))
        initListeners()
    }

    override fun onStop() {
        super.onStop()
        operationSnackbar.dismissIfVisible()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun initListeners() {
        binding.appointmentDateInput.setOnClickListener {
            displayDatePickerDialog()
        }

        viewModel.validDate.observe(this, Observer {
            binding.appointmentDateLayout.error = if (it == true) "" else getString(R.string.invalid_date)
        })

        viewModel.operationResult.observe(this, Observer { result ->
            if (result is OperationResult.Success) {
                displaySnackbar(getString(R.string.appointment_created))
            } else if (result is OperationResult.Error) {
                result.message?.let {
                    displaySnackbar(it)
                }
            }
        })

        binding.registerAppointmentButton.setOnClickListener {
            if (!NetworkManager.isNetworkAvailable(this)) {
                displaySnackbar(getString(R.string.no_internet_appointment_will_be_created))
            }
            viewModel.createNewAppointment()
            clearFields()
        }
    }

    private fun displayDatePickerDialog() {
        val currentCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.newInstance(
            { _, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance().apply {
                    setCalendarToDayStart(this, year, monthOfYear, dayOfMonth)
                }
                displayTimePickerDialog(calendar.timeInMillis)
            },
            currentCalendar.get(Calendar.YEAR),
            currentCalendar.get(Calendar.MONTH),
            currentCalendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.disabledDays = viewModel.availableAppointmentsDetails.unselectableDays.toTypedArray()
        datePicker.minDate = Calendar.getInstance()
        datePicker.maxDate = Calendar.getInstance().apply {
            timeInMillis += TWO_MONTHS_TIME_IN_MILLIS
        }
        datePicker.show(fragmentManager, APPOINTMENT_DATE_DIALOG_TAG)
    }

    private fun displayTimePickerDialog(dayTime: Long) {
        val dayBeginningTime = Calendar.getInstance().apply {
            timeInMillis = dayTime
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val timePicker = TimePickerDialog.newInstance({ _, hour, minute, _ ->
            val selectedDayTimestamp = Calendar.getInstance().apply {
                timeInMillis = dayTime
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }.timeInMillis
            viewModel.appointmentTime.set(selectedDayTimestamp)
        }, true)
        viewModel.getSelectableTimes(dayTime)?.let {
            timePicker.setSelectableTimes(it.toTypedArray())
        }
        viewModel.availableAppointmentsDetails.unselectableTimesForDays[dayBeginningTime]?.let {
            timePicker.setDisabledTimes(it.toTypedArray())
        }
        timePicker.show(fragmentManager, APPOINTMENT_TIME_DIALOG_TAG)

    }

    private fun clearFields() {
        binding.appointmentDateInput.setText("")
        binding.appointmentDetailsInput.setText("")
    }

    private fun setCalendarToDayStart(calendar: Calendar, year: Int, month: Int, dayOfMonth: Int) {
        calendar.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
    }

    private fun displaySnackbar(message: String) {
        operationSnackbar = operationSnackbar.newLongSnackbar(binding.root, message)
    }

    companion object {
        private const val MEDIC_NAME = "medicName"
        private const val MEDIC_ID = "medicId"
        private const val MEDICAL_SPECIALTY_NAME = "medicalSpecialtyName"
        private const val APPOINTMENT_DATE_DIALOG_TAG = "appointmentDateDialogTag"
        private const val APPOINTMENT_TIME_DIALOG_TAG = "appointmentTimeDialogTag"
        private const val TWO_MONTHS_TIME_IN_MILLIS: Long = 31 * 24 * 3600 * 1000L

        fun getStartIntent(context: Context, medicName: String, medicalSpecialty: String, medicId: String): Intent =
            Intent(context, AppointmentCreateActivity::class.java)
                .putExtra(MEDIC_NAME, medicName).putExtra(MEDIC_ID, medicId).putExtra(MEDICAL_SPECIALTY_NAME, medicalSpecialty)
    }
}