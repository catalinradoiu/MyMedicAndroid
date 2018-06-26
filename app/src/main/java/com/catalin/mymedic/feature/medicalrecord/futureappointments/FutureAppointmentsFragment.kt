package com.catalin.mymedic.feature.medicalrecord.futureappointments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.PatientOwnAppointmentsBinding
import com.catalin.mymedic.R
import com.catalin.mymedic.data.AppointmentStatus
import com.catalin.mymedic.feature.appointmentdetails.AppointmentDetailsActivity
import com.catalin.mymedic.feature.shared.AppointmentCancelationDialog
import com.catalin.mymedic.feature.shared.AppointmentCancelationViewModel
import com.catalin.mymedic.utils.NetworkManager
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.newLongSnackbar
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/17/2018
 */
class FutureAppointmentsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: FutureAppointmentsViewModel.Factory

    private lateinit var viewModel: FutureAppointmentsViewModel
    private lateinit var binding: PatientOwnAppointmentsBinding
    private lateinit var futureAppointmentsAdapter: FutureAppointmentsAdapter

    private var operationSnackbar: Snackbar? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context?.applicationContext as MyMedicApplication).applicationComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.patient_own_appointments_fragment, container, false)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(FutureAppointmentsViewModel::class.java)
        futureAppointmentsAdapter = FutureAppointmentsAdapter(viewModel.getCurrentUserId())
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.patientOwnAppointmentsRecycler.apply {
            adapter = futureAppointmentsAdapter
            layoutManager = LinearLayoutManager(context)
        }
        initListeners()
        viewModel.initMedicalAppointments()
    }

    private fun initListeners() {
        binding.patientOwnAppointmentsStateLayout.setOnErrorTryAgainListener {
            viewModel.initMedicalAppointments()
        }

        viewModel.patientAppointments.observe(this, Observer {
            futureAppointmentsAdapter.appointmentsList = ArrayList(it)
        })

        viewModel.appointmentCancelResult.observe(this, Observer {
            when (it) {
                is OperationResult.Success -> displaySnackbar(getString(R.string.appointment_canceled))
                is OperationResult.Error -> displaySnackbar(it.message.orEmpty())
            }
        })

        futureAppointmentsAdapter.setOnOwnAppointmentCancelListener(object : FutureAppointmentsAdapter.OnOwnAppointmentCancelListener {
            override fun onCancel(position: Int) {
                val appointmentCancelationViewModel = AppointmentCancelationViewModel()
                val appointmentCancelDialog = AppointmentCancelationDialog.getInstance(appointmentCancelationViewModel).apply {
                    setOnConfirmationClickListener {
                        viewModel.cancelAppointment(
                            futureAppointmentsAdapter.appointmentsList[position],
                            appointmentCancelationViewModel.cancelationReason.get().orEmpty(),
                            AppointmentStatus.CANCELED_BY_PATIENT
                        )
                        if (!NetworkManager.isNetworkAvailable(binding.root.context)) {
                            displaySnackbar(getString(R.string.no_internet_appointment_will_be_updated))
                        }
                    }
                }
                appointmentCancelDialog.show(fragmentManager, APPOINTMENT_CANCEL_DIALOG_TAG)
            }

        })

        futureAppointmentsAdapter.setOnPatientAppointmentCancelListener(object : FutureAppointmentsAdapter.OnPatientappointmentCancelListener {
            override fun onCancel(position: Int) {
                val appointmentCancelationViewModel = AppointmentCancelationViewModel()
                val appointmentCancelDialog = AppointmentCancelationDialog.getInstance(appointmentCancelationViewModel).apply {
                    setOnConfirmationClickListener {
                        viewModel.cancelAppointment(
                            futureAppointmentsAdapter.appointmentsList[position],
                            appointmentCancelationViewModel.cancelationReason.get().orEmpty(),
                            AppointmentStatus.CANCELED_BY_MEDIC
                        )
                        if (!NetworkManager.isNetworkAvailable(binding.root.context)) {
                            displaySnackbar(getString(R.string.no_internet_appointment_will_be_updated))
                        }
                    }
                }
                appointmentCancelDialog.show(fragmentManager, APPOINTMENT_CANCEL_DIALOG_TAG)
            }

        })

        futureAppointmentsAdapter.setOnAppointmentClickListener(object : FutureAppointmentsAdapter.OnAppointmentClickListener {
            override fun onClick(position: Int) {
                context?.let {
                    startActivity(AppointmentDetailsActivity.getStartIntent(it, futureAppointmentsAdapter.appointmentsList[position].id))
                }
            }

        })
    }

    private fun displaySnackbar(message: String) {
        operationSnackbar.newLongSnackbar(binding.root, message)
    }

    companion object {
        private const val APPOINTMENT_CANCEL_DIALOG_TAG = "appointmentCancelDialogTag"
    }
}