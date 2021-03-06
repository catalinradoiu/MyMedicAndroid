package com.catalin.mymedic.feature.medicalrecord.search.specialties

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
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.MedicalSpecialtiesSearchFragmentBinding
import com.catalin.mymedic.feature.medicalrecord.search.medics.MedicsSearchActivity
import com.catalin.mymedic.utils.extension.dismissIfVisible
import com.catalin.mymedic.utils.extension.onPropertyChanged
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/23/2018
 */
class MedicalSpecialtiesSearchFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: MedicalSpecialtiesSearchViewModel.MedicalSpecialtiesViewModelFactory

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private lateinit var binding: MedicalSpecialtiesSearchFragmentBinding
    private lateinit var viewModel: MedicalSpecialtiesSearchViewModel

    private lateinit var medicalSpecialtiesAdapter: MedicalSpecialtiesAdapter
    private var operationSnackbar: Snackbar? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context?.applicationContext as MyMedicApplication).applicationComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.medical_specialties_search_fragment, container, false)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MedicalSpecialtiesSearchViewModel::class.java)
        viewModel.initMedicalSpecialties()
        binding.viewModel = viewModel
        medicalSpecialtiesAdapter = MedicalSpecialtiesAdapter(firebaseStorage)
        medicalSpecialtiesAdapter.setOnItemClickListener(object : MedicalSpecialtiesAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                context?.let {
                    val selectedSpecialty = medicalSpecialtiesAdapter.getMedicalSpecialty(position)
                    startActivity(MedicsSearchActivity.getStartIntent(it, selectedSpecialty.id, selectedSpecialty.name))
                }
            }

        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.medicalSpecialtiesRecycler.adapter = medicalSpecialtiesAdapter
        binding.medicalSpecialtiesRecycler.layoutManager = LinearLayoutManager(context)
        initListeners()
    }

    override fun onStop() {
        super.onStop()
        operationSnackbar?.dismissIfVisible()
    }

    private fun initListeners() {
        viewModel.medicalSpecialtiesList.onPropertyChanged { value ->
            medicalSpecialtiesAdapter.addMedicalSpecialties(value)
        }

        binding.searchStateLayout.setOnErrorTryAgainListener {
            viewModel.initMedicalSpecialties()
        }
    }
}