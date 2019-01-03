package com.catalin.mymedic.feature.home

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.HomeActivityBinding
import com.catalin.mymedic.feature.chat.ConversationsListFragment
import com.catalin.mymedic.feature.medicalrecord.MedicalRecordFragment
import com.catalin.mymedic.feature.profile.ProfileFragment
import com.catalin.mymedic.feature.settings.SettingsFragment

/**
 * @author catalinradoiu
 * @since 4/15/2018
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.home_activity)
        supportFragmentManager.beginTransaction().replace(R.id.home_fragment_container, MedicalRecordFragment(), CURRENT_FRAGMENT).commit()
        initListeners()
    }

    private fun initListeners() {
        binding.homeBottomNavigation.setOnNavigationItemSelectedListener { item ->
            val oldFragment = supportFragmentManager.findFragmentByTag(CURRENT_FRAGMENT)
            when (item.itemId) {
                R.id.main_navigation_medical_record -> {
                    if (oldFragment !is MedicalRecordFragment) {
                        supportFragmentManager.beginTransaction().replace(R.id.home_fragment_container, MedicalRecordFragment(), CURRENT_FRAGMENT).commit()
                    }
                }
                R.id.main_navigation_chat -> {
                    if (oldFragment !is ConversationsListFragment) {
                        supportFragmentManager.beginTransaction().replace(R.id.home_fragment_container, ConversationsListFragment(), CURRENT_FRAGMENT).commit()
                    }
                }
                R.id.main_navigation_profile -> {
                    if (oldFragment !is ProfileFragment) {
                        supportFragmentManager.beginTransaction().replace(R.id.home_fragment_container, ProfileFragment(), CURRENT_FRAGMENT).commit()
                    }
                }
                R.id.main_navigation_settings -> {
                    if (oldFragment !is SettingsFragment) {
                        supportFragmentManager.beginTransaction().replace(R.id.home_fragment_container, SettingsFragment(), CURRENT_FRAGMENT).commit()
                    }
                }
            }
            true
        }
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, HomeActivity::class.java)

        private const val CURRENT_FRAGMENT = "currentFragment"
    }
}