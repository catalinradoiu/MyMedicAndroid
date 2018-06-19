package com.catalin.mymedic.feature.medicalrecord.ownappointments

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.catalin.mymedic.PatientAppointmentHeaderBinding
import com.catalin.mymedic.PatientItemBinding
import com.catalin.mymedic.R
import com.catalin.mymedic.data.AppointmentStatus
import com.catalin.mymedic.data.MedicalAppointment

/**
 * @author catalinradoiu
 * @since 6/17/2018
 */
class PatientOwnAppointmentsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var appointmentsList = ArrayList<MedicalAppointment>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.patient_appointment_header_view -> PatientAppointmentHeader(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
        )
        else -> PatientAppointmentItem(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.patient_own_appointment_item, parent, false),
            PatientAppointmentItemViewModel()
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PatientAppointmentItem) {
            holder.bind(appointmentsList[position - 1])
        }
    }

    override fun getItemViewType(position: Int) =
        if (position == HEADER_ITEM_POSITION) R.layout.patient_appointment_header_view else R.layout.patient_own_appointment_item

    override fun getItemCount() = if (appointmentsList.size != 0) appointmentsList.size + 1 else 0

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

    class PatientAppointmentHeader(binding: PatientAppointmentHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private const val HEADER_ITEM_POSITION = 0
    }
}