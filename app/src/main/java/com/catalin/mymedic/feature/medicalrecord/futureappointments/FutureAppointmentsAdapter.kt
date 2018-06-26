package com.catalin.mymedic.feature.medicalrecord.futureappointments

import android.databinding.DataBindingUtil
import android.support.v4.content.ContextCompat
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

    private var onOwnAppointmentCancelListener: OnOwnAppointmentCancelListener? = null
    private var onPatientAppointmentCancelListener: OnPatientappointmentCancelListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        R.layout.patient_appointment_header_view -> PatientAppointmentHeader(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
        )
        R.layout.patient_own_appointment_item -> PatientAppointmentItem(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false),
            PatientAppointmentItemViewModel(),
            onOwnAppointmentCancelListener
        )
        R.layout.incoming_pattients_header_view -> IncomingAppointmentsHeader(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)
        )
        else -> MedicIncomingAppointmentItem(
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.incoming_patient_item, parent, false),
            IncomingPatientItemViewModel(),
            onPatientAppointmentCancelListener
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

    fun setOnOwnAppointmentCancelListener(onOwnAppointmentCancelListener: OnOwnAppointmentCancelListener) {
        this.onOwnAppointmentCancelListener = onOwnAppointmentCancelListener
    }

    fun setOnPatientAppointmentCancelListener(onPatientappointmentCancelListener: OnPatientappointmentCancelListener?) {
        this.onPatientAppointmentCancelListener = onPatientAppointmentCancelListener
    }

    interface OnOwnAppointmentCancelListener {
        fun onCancel(position: Int)
    }

    interface OnPatientappointmentCancelListener {
        fun onCancel(position: Int)
    }

    class PatientAppointmentItem(
        private val binding: PatientItemBinding,
        private val viewModel: PatientAppointmentItemViewModel,
        onOwnAppointmentCancelListener: OnOwnAppointmentCancelListener?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = viewModel
            binding.appointmentCancelButton.setOnClickListener {
                val position = adapterPosition
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onOwnAppointmentCancelListener?.onCancel(position)
                }
            }
        }

        fun bind(appointment: MedicalAppointment) {
            viewModel.apply {
                appointmentTime.set(appointment.dateTime)
                specialtyName.set(appointment.specialtyName)
                medicName.set(appointment.medicName)
                status.set(appointment.status)
                appointmentStatusString.set(
                    when (appointment.status) {
                        AppointmentStatus.AWAITING -> itemView.context.getString(R.string.awaiting)
                        AppointmentStatus.CONFIRMED -> itemView.context.getString(R.string.confirmed)
                        AppointmentStatus.REJECTED -> itemView.context.getString(R.string.rejected)
                        AppointmentStatus.CANCELED_BY_PATIENT, AppointmentStatus.CANCELED_BY_MEDIC -> itemView.context.getString(R.string.canceled)
                    }
                )
            }
            binding.patientAppointmentStatus.setTextColor(
                when (appointment.status) {
                    AppointmentStatus.AWAITING -> ContextCompat.getColor(itemView.context, R.color.yellow)
                    AppointmentStatus.CONFIRMED -> ContextCompat.getColor(itemView.context, R.color.light_green)
                    else -> ContextCompat.getColor(itemView.context, R.color.colorPrimary)
                }
            )
        }
    }

    class MedicIncomingAppointmentItem(
        binding: IncomingPatientBinding,
        private val viewModel: IncomingPatientItemViewModel,
        onPatientappointmentCancelListener: OnPatientappointmentCancelListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = viewModel
            binding.appointmentCancelButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onPatientappointmentCancelListener?.onCancel(position)
                }
            }
        }

        fun bind(appointment: MedicalAppointment) {
            viewModel.apply {
                patientName.set(appointment.patientName)
                appointmentTime.set(appointment.dateTime)

            }
        }
    }

    class PatientAppointmentHeader(binding: PatientAppointmentHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    class IncomingAppointmentsHeader(binding: IncomingPatientsHeaderBinding) : RecyclerView.ViewHolder(binding.root)
}