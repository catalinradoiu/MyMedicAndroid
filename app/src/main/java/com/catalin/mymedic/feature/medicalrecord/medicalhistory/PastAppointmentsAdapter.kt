package com.catalin.mymedic.feature.medicalrecord.medicalhistory

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.catalin.mymedic.*
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.feature.shared.AppointmentDiffCallback
import com.catalin.mymedic.utils.Constants

/**
 * @author catalinradoiu
 * @since 6/26/2018
 */
class PastAppointmentsAdapter(private val userId: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var appointmentsList = ArrayList<MedicalAppointment>()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(AppointmentDiffCallback(value, field))
            field.clear()
            field.addAll(value)
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.past_patients_header_view -> PastPatientsHeaderViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
        )
        R.layout.past_appointments_header_view -> PastAppointmentsHeaderViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
        )
        R.layout.past_appointment_item -> PastAppointmentViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false),
            PastAppointmentItemViewModel()
        )
        else -> PastPatientViewHolder(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.past_patient_item, parent, false),
            PastPatientItemViewModel()
        )
    }

    override fun getItemCount() = appointmentsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PastPatientViewHolder -> holder.bind(appointmentsList[position])
            is PastAppointmentViewHolder -> holder.bind(appointmentsList[position])
        }
    }

    override fun getItemViewType(position: Int) = when (appointmentsList[position].id) {
        Constants.OWN_APPOINTMENTS_HEADER_ID -> R.layout.past_appointments_header_view
        Constants.PATIENT_APPOINTMENTS_HEADER_ID -> R.layout.past_patients_header_view
        else -> {
            if (appointmentsList[position].medicId == userId) {
                R.layout.past_patient_item
            } else {
                R.layout.past_appointment_item
            }
        }
    }

    class PastAppointmentsHeaderViewHolder(binding: PastAppointmentsHeaderViewBinding) : RecyclerView.ViewHolder(binding.root)

    class PastPatientsHeaderViewHolder(binding: PastPatientsHeaderViewBinding) : RecyclerView.ViewHolder(binding.root)

    class PastAppointmentViewHolder(binding: PastAppointmentItemBinding, private val viewModel: PastAppointmentItemViewModel) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = viewModel
        }

        fun bind(appointment: MedicalAppointment) {
            viewModel.apply {
                appointmentDate.set(appointment.dateTime)
                medicName.set(appointment.medicName)
                appointmentStatus.set(appointment.status)
            }
        }
    }

    class PastPatientViewHolder(binding: PastPatientItemBinding, private val viewModel: PastPatientItemViewModel) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = viewModel
        }

        fun bind(appointment: MedicalAppointment) {
            viewModel.apply {
                appointmentDate.set(appointment.dateTime)
                patientName.set(
                    appointment.patientName
                )
                appointmentStatus.set(appointment.status)
            }
        }
    }

}