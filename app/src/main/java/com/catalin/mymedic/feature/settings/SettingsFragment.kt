package com.catalin.mymedic.feature.settings

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.SettingsFragmentBinding
import com.catalin.mymedic.feature.authentication.login.LoginActivity
import javax.inject.Inject


/**
 * @author catalinradoiu
 * @since 4/26/2018
 */
class SettingsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: SettingsViewModel.Factory

    private lateinit var binding: SettingsFragmentBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context.applicationContext as MyMedicApplication).applicationComponent.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingsViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity?)?.supportActionBar?.apply {
            title = getString(R.string.settings)
            elevation = resources.getDimension(R.dimen.standard_elevation)
        }
        binding.apply {
            messagesNotificationsSwitch.isChecked = viewModel.isMessageNotificationEnabled()
            appointmentsNotificationsSwitch.isChecked = viewModel.isAppointmentNotificationEnabled()
        }
        initListeners()
    }

    private fun initListeners() {
        binding.logOut.setOnClickListener {
            viewModel.logOutUser()
            context?.let {
                startActivity(LoginActivity.getStartIntent(it))
                activity?.finish()
            }
        }

        binding.messagesNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setMessageNotificationState(isChecked)
        }

        binding.appointmentsNotificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAppointmentNotificationState(isChecked)
        }

        binding.appointmentsNotificationsFrame.setOnClickListener {
            val newValue = !binding.appointmentsNotificationsSwitch.isChecked
            binding.appointmentsNotificationsSwitch.isChecked = newValue
            viewModel.setAppointmentNotificationState(newValue)
        }

        binding.messagesNotificationsFrame.setOnClickListener {
            val newValue = !binding.messagesNotificationsSwitch.isChecked
            binding.messagesNotificationsSwitch.isChecked = newValue
            viewModel.setMessageNotificationState(newValue)
        }

        binding.aboutUsInformation.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(ABOUT_US_URL)))
        }

        binding.privacyPolicyInformation.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(PRIVACY_POLICY_URL)))
        }
    }

    companion object {
        private const val ABOUT_US_URL = "http://www.cs.ubbcluj.ro/"
        private const val PRIVACY_POLICY_URL = "https://www.eugdpr.org/"
    }
}