package com.catalin.mymedic.feature.medicalrecord.awaitingappointments

import android.annotation.SuppressLint
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.catalin.mymedic.data.MedicalAppointment
import com.catalin.mymedic.databinding.AwaitingAppointmentItemBinding
import com.catalin.mymedic.feature.shared.AppointmentDiffCallback
import java.text.SimpleDateFormat

/**
 * @author catalinradoiu
 * @since 6/15/2018
 */
class AwaitingAppointmentsAdapter :
    RecyclerView.Adapter<AwaitingAppointmentsAdapter.AwaitingAppointmentViewHolder>() {

    var awaitingAppointments = ArrayList<MedicalAppointment>()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(AppointmentDiffCallback(value, field))
            field.clear()
            field.addAll(value)
            diffResult.dispatchUpdatesTo(this)
        }

    private var onAppointmentStatusChangeListener: OnAppointmentStatusChangeListener? = null

    override fun getItemCount() = awaitingAppointments.size

    override fun onBindViewHolder(holder: AwaitingAppointmentViewHolder, position: Int) {
        holder.bind(awaitingAppointments[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AwaitingAppointmentViewHolder =
        AwaitingAppointmentViewHolder(
            AwaitingAppointmentItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onAppointmentStatusChangeListener
        )

    fun setOnAppointmentStatusChangeListener(onAppointmentStatusChangeListener: OnAppointmentStatusChangeListener) {
        this.onAppointmentStatusChangeListener = onAppointmentStatusChangeListener
    }

    fun removeAppointment(position: Int) {
        awaitingAppointments.removeAt(position)
    }

    interface OnAppointmentStatusChangeListener {
        fun onAccept(position: Int)
        fun onReject(position: Int)
        fun onClick(position: Int)
    }

    class AwaitingAppointmentViewHolder(
        private val binding: AwaitingAppointmentItemBinding,
        private val onAppointmentStatusChangeListener: OnAppointmentStatusChangeListener?
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.confirmAppoitmentButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAppointmentStatusChangeListener?.onAccept(position)
                }
            }

            binding.rejectAppointmentButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAppointmentStatusChangeListener?.onReject(position)
                }
            }

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onAppointmentStatusChangeListener?.onClick(position)
                }
            }
        }

        @SuppressLint("SimpleDateFormat")
        fun bind(appointment: MedicalAppointment) {
            binding.appointmentAuthor.text = appointment.patientName
            binding.appointmentTime.text =
                    SimpleDateFormat("dd/MM/YYY HH:mm").format(appointment.dateTime)
        }

    }
}