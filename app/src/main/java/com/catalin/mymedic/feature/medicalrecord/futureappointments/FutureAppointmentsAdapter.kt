package com.catalin.mymedic.feature.medicalrecord.futureappointments

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.catalin.mymedic.*
import com.catalin.mymedic.data.AppointmentStatus
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.utils.Constants

/**
 * @author catalinradoiu
 * @since 6/17/2018
 */
class FutureAppointmentsAdapter(private val userId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var appointmentsList = ArrayList<MedicalAppointment>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.patient_appointment_header_view -> PatientAppointmentHeader(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
        )
        R.layout.patient_own_appointment_item -> PatientAppointmentItem(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false),
            PatientAppointmentItemViewModel()
        )
        R.layout.incoming_pattients_header_view -> IncomingAppointmentsHeader(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
        )
        else -> MedicIncomingAppointmentItem(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.incoming_patient_item, parent, false),
            IncomingPatientItemViewModel()
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PatientAppointmentItem -> holder.bind(appointmentsList[position])
            is MedicIncomingAppointmentItem -> holder.bind(appointmentsList[position])
        }
    }

    override fun getItemViewType(position: Int) = when (appointmentsList[position].id) {
        Constants.OWN_APPOINTMENTS_HEADER_ID -> R.layout.patient_appointment_header_view
        Constants.PATIENT_APPOINTMENTS_HEADER_ID -> R.layout.incoming_pattients_header_view
        else -> {
            if (appointmentsList[position].medicId == userId) {
                R.layout.incoming_patient_item
            } else {
                R.layout.patient_own_appointment_item
            }
        }
    }

    override fun getItemCount() = appointmentsList.size

    class PatientAppointmentItem(binding: PatientItemBinding, private val viewModel: PatientAppointmentItemViewModel) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = viewModel
        }

        fun bind(appointment: MedicalAppointment) {
            viewModel.apply {
                appointmentTime.set(appointment.dateTime)
                specialtyName.set(appointment.specialtyName)
                medicName.set(appointment.medicName)
                status.set(appointment.status)
                appointmentStatusString.set(
                    if (appointment.status == AppointmentStatus.AWAITING) itemView.context.getString(
                        R.string.awaiting
                    ) else itemView.context.getString(
                        R.string.confirmed
                    )
                )
            }
        }
    }

    class MedicIncomingAppointmentItem(binding: IncomingPatientBinding, viewModel: IncomingPatientItemViewModel) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = viewModel
        }

        fun bind(appointment: MedicalAppointment) {

        }
    }

    class PatientAppointmentHeader(binding: PatientAppointmentHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    class IncomingAppointmentsHeader(binding: IncomingPatientsHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val HEADER_ITEM_POSITION = 0
    }
}