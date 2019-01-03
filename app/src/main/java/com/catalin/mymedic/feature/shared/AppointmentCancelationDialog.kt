package com.catalin.mymedic.feature.shared

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catalin.mymedic.AppointmentCancelationBinding
import com.catalin.mymedic.R


/**
 * @author catalinradoiu
 * @since 6/19/2018
 */
class AppointmentCancelationDialog : DialogFragment() {

    private lateinit var binding: AppointmentCancelationBinding
    private lateinit var viewModel: AppointmentCancelationViewModel
    private var onConfirmationButtonListener: () -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.appointment_cancel_dialog_layout, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.confirmationButton.setOnClickListener {
            if (viewModel.cancelationReason.get().orEmpty().isEmpty()) {
                binding.cancelationReasonLayout.error = context?.getString(R.string.reason_cannot_be_empty)
            } else {
                onConfirmationButtonListener()
                dismiss()
            }
        }
        binding.viewModel = viewModel
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    fun setOnConfirmationClickListener(onConfirmationButtonListener: () -> Unit) {
        this.onConfirmationButtonListener = onConfirmationButtonListener
    }

    companion object {
        fun getInstance(viewModel: AppointmentCancelationViewModel) = AppointmentCancelationDialog().apply { this.viewModel = viewModel }
    }

}