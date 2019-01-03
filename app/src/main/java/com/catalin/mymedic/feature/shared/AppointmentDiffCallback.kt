package com.catalin.mymedic.feature.shared

import android.support.v7.util.DiffUtil
import com.catalin.mymedic.data.MedicalAppointment

/**
 * @author catalinradoiu
 * @since 6/26/2018
 */
class AppointmentDiffCallback(private var newAppointments: List<MedicalAppointment>, private var oldAppointments: List<MedicalAppointment>) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldAppointments[oldItemPosition].id === newAppointments[newItemPosition].id

    override fun getOldListSize() = oldAppointments.size

    override fun getNewListSize() = newAppointments.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldAppointments[oldItemPosition].id === newAppointments[newItemPosition].id
}