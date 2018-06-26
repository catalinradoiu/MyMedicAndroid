package com.catalin.mymedic.feature.medicalrecord.medicalhistory

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catalin.mymedic.MedicalHistoryBinding
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/26/2018
 */
class MedicalHistoryFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: MedicalHistoryViewModel.Factory

    private lateinit var viewModel: MedicalHistoryViewModel
    private lateinit var binding: MedicalHistoryBinding
    private lateinit var pastAppointmentsAdapter: PastAppointmentsAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MyMedicApplication).applicationComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.medical_history_fragment, container, false)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MedicalHistoryViewModel::class.java)
        binding.viewModel = viewModel
        pastAppointmentsAdapter = PastAppointmentsAdapter(viewModel.getCurrentUserId())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pastAppointmentsRecycler.apply {
            adapter = pastAppointmentsAdapter
            layoutManager = LinearLayoutManager(context)
        }
        viewModel.initPastMedicalAppointments()
        initListeners()
    }

    private fun initListeners() {
        binding.medicalHistoryStateLayout.setOnErrorTryAgainListener {
            viewModel.initPastMedicalAppointments()
        }

        viewModel.medicalAppointments.observe(this, Observer {
            it?.let { list ->
                pastAppointmentsAdapter.appointmentsList = ArrayList(list)
            }
        })
    }
}