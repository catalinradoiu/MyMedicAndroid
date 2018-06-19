package com.catalin.mymedic.feature.medicalrecord.awaitingappointments

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
import com.catalin.mymedic.AwaitingAppointmentsBinding
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.data.AppointmentStatus
import com.catalin.mymedic.feature.shared.AppointmentCancelationDialog
import com.catalin.mymedic.feature.shared.AppointmentCancelationViewModel
import com.catalin.mymedic.utils.NetworkManager
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.dismissIfVisible
import com.catalin.mymedic.utils.extension.newLongSnackbar
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/15/2018
 */
class AwaitingAppointmentsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: AwaitingAppointmentsViewModel.Factory

    private lateinit var binding: AwaitingAppointmentsBinding
    private lateinit var viewModel: AwaitingAppointmentsViewModel

    private var operationSnackbar: Snackbar? = null

    private val awaitingAppointmentsAdapter = AwaitingAppointmentsAdapter()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context?.applicationContext as MyMedicApplication).applicationComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.awaiting_appointments_fragment, container, false)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AwaitingAppointmentsViewModel::class.java)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.awaitingAppointmentsRecycler.apply {
            adapter = awaitingAppointmentsAdapter
            layoutManager = LinearLayoutManager(context)
        }
        viewModel.initAwaitingAppointments()
        initListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        operationSnackbar.dismissIfVisible()
    }

    private fun initListeners() {
        binding.awaitingAppointmentsStateLayout.setOnErrorTryAgainListener {
            viewModel.initAwaitingAppointments()
        }

        viewModel.awaitingAppointments.observe(this, Observer {
            awaitingAppointmentsAdapter.awaitingAppointments = ArrayList(it)
        })

        viewModel.operationResult.observe(this, Observer {
            when (it) {
                is OperationResult.Success -> displaySnackbar(getString(R.string.appointment_updated_successfully))
                is OperationResult.Error -> displaySnackbar(getString(R.string.something_went_wrong))
            }
        })

        awaitingAppointmentsAdapter.setOnAppointmentStatusChangeListener(object :
            AwaitingAppointmentsAdapter.OnAppointmentStatusChangeListener {
            override fun onReject(position: Int) {
                context?.let {
                    val dialogViewModel = AppointmentCancelationViewModel()
                    val rejectionReasonDialog = AppointmentCancelationDialog.getInstance(dialogViewModel).apply {
                        setOnConfirmationClickListener {
                            viewModel.cancelAppointment(
                                awaitingAppointmentsAdapter.awaitingAppointments[position],
                                AppointmentStatus.REJECTED,
                                dialogViewModel.cancelationReason.get().orEmpty()
                            )
                            if (!NetworkManager.isNetworkAvailable(binding.root.context)) {
                                displaySnackbar(getString(R.string.no_internet_appointment_will_be_updated))
                                awaitingAppointmentsAdapter.removeAppointment(position)
                            }
                        }
                    }
                    rejectionReasonDialog.show(fragmentManager, CANCEL_APPOINTMENT_DIALOG_TAG)
                }

            }

            override fun onAccept(position: Int) {
                if (!NetworkManager.isNetworkAvailable(binding.root.context)) {
                    displaySnackbar(getString(R.string.no_internet_appointment_will_be_updated))
                    awaitingAppointmentsAdapter.removeAppointment(position)
                }
                viewModel.approveAppointment(
                    awaitingAppointmentsAdapter.awaitingAppointments[position],
                    AppointmentStatus.CONFIRMED
                )
            }

        })
    }

    private fun displaySnackbar(message: String) {
        operationSnackbar.newLongSnackbar(binding.root, message)
    }

    companion object {
        private const val CANCEL_APPOINTMENT_DIALOG_TAG = "cancelAppointmentDialog"
    }
}