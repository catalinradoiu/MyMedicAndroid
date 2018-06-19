package com.catalin.mymedic.feature.medicalrecord.ownappointments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.PatientOwnAppointmentsBinding
import com.catalin.mymedic.R
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/17/2018
 */
class PatientOwnAppointmentsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: PatientOwnAppointmentsViewModel.Factory

    private lateinit var viewModel: PatientOwnAppointmentsViewModel
    private lateinit var binding: PatientOwnAppointmentsBinding
    private val patientOwnAppointmentsAdapter = PatientOwnAppointmentsAdapter()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context?.applicationContext as MyMedicApplication).applicationComponent.inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.patient_own_appointments_fragment,
            container,
            false
        )
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(PatientOwnAppointmentsViewModel::class.java)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.patientOwnAppointmentsRecycler.apply {
            adapter = patientOwnAppointmentsAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        }
        initListeners()
        viewModel.initMedicalAppointments()
    }

    private fun initListeners() {
        viewModel.patientAppointments.observe(this, Observer {
            patientOwnAppointmentsAdapter.appointmentsList = ArrayList(it)
        })
    }
}