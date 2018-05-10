package com.catalin.mymedic.feature.medicalrecord

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catalin.mymedic.R

/**
 * @author catalinradoiu
 * @since 4/30/2018
 */
class MedicalRecordPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_activity, container, false)
    }
}