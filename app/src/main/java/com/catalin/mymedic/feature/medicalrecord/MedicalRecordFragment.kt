package com.catalin.mymedic.feature.medicalrecord

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.MedicalRecordFragmentBinding
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.utils.Constants
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 4/26/2018
 */
class MedicalRecordFragment : Fragment() {

    private lateinit var binding: MedicalRecordFragmentBinding

    @Inject
    lateinit var prefsManager: SharedPreferencesManager

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        (context?.applicationContext as MyMedicApplication).applicationComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.medical_record_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.let {
            it.title = getString(R.string.medical_record)
            it.elevation = 0f
        }
        binding.medicalRecordTabs.elevation = resources.getDimension(R.dimen.standard_elevation)
        initPager()
    }

    private fun initPager() {
        binding.medicalRecordPager.adapter =
                if (prefsManager.currentUserSpecialty == Constants.PATIENT) MedicalRecordViewPagerPatient(childFragmentManager).apply {
                    pageTitles = resources.getStringArray(R.array.medical_record_patient_options).toList()
                } else
                    MedicalRecordViewPagerMedic(childFragmentManager).apply {
                        pageTitles = resources.getStringArray(R.array.medical_record_medic_options).toList()
                    }
        binding.medicalRecordTabs.setupWithViewPager(binding.medicalRecordPager)
        binding.medicalRecordTabs.tabMode = if (prefsManager.currentUserSpecialty != Constants.PATIENT) TabLayout.MODE_SCROLLABLE else TabLayout.MODE_FIXED
    }
}