package com.catalin.mymedic.feature.appointmentdetails

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.catalin.mymedic.AppointmentDetailsBinding
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.feature.chat.conversationdetails.ConversationDetailsActivity
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/26/2018
 */
class AppointmentDetailsActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: AppointmentDetailsViewModel.Factory

    private lateinit var viewModel: AppointmentDetailsViewModel
    private lateinit var binding: AppointmentDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as MyMedicApplication).applicationComponent.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.appointment_details_activity)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(AppointmentDetailsViewModel::class.java)
        binding.viewModel = viewModel
        supportActionBar?.title = getString(R.string.appointment_details)
        viewModel.getAppointmentDetails(intent.appointmentId)
        initListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun initListeners() {
        binding.messageButton.setOnClickListener {
            val currentUserId = viewModel.getCurrentUserId()
            val currentAppointment = viewModel.appointment.get()
            val otherParticipantId = if (currentUserId == currentAppointment?.medicId) currentAppointment.patientId else currentAppointment?.medicId.orEmpty()
            val otherParticipantName =
                if (currentUserId == currentAppointment?.medicId) currentAppointment.patientName else currentAppointment?.medicName.orEmpty()
            startActivity(
                ConversationDetailsActivity.getStartIntent(
                    this,
                    otherParticipantId,
                    FirebaseDatabaseConfig.DEFAULT_USER_IMAGE_LOCATION,
                    otherParticipantName
                )
            )
        }
    }

    companion object {

        private const val APPOINTMENT_ID = "appointmentId"

        private val Intent.appointmentId
            get() = getStringExtra(APPOINTMENT_ID)

        fun getStartIntent(context: Context, appointmentId: String): Intent =
            Intent(context, AppointmentDetailsActivity::class.java).putExtra(APPOINTMENT_ID, appointmentId)
    }
}