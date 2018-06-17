package com.catalin.mymedic.feature.medicalrecord.ownappointments

import android.content.Context
import android.support.v4.app.Fragment
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.PatientOwnAppointmentsBinding

/**
 * @author catalinradoiu
 * @since 6/17/2018
 */
class PatientOwnAppointmentsFragment : Fragment() {

    private lateinit var binding: PatientOwnAppointmentsBinding

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context?.applicationContext as MyMedicApplication).applicationComponent.inject(this)
    }
}