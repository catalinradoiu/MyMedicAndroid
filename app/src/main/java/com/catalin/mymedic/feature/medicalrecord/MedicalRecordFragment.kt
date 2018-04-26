package com.catalin.mymedic.feature.medicalrecord

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.MedicalRecordFragmentBinding

/**
 * @author catalinradoiu
 * @since 4/26/2018
 */
class MedicalRecordFragment : Fragment() {

    private lateinit var binding: MedicalRecordFragmentBinding

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
    }
}