package com.catalin.mymedic.feature.medicalrecord

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.catalin.mymedic.feature.medicalrecord.awaitingappointments.AwaitingAppointmentsFragment
import com.catalin.mymedic.feature.medicalrecord.futureappointments.FutureAppointmentsFragment
import com.catalin.mymedic.feature.medicalrecord.search.specialties.MedicalSpecialtiesSearchFragment

/**
 * @author catalinradoiu
 * @since 4/30/2018
 */
class MedicalRecordViewPagerMedic(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager) {

    var pageTitles: List<String>? = null

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> AwaitingAppointmentsFragment()
        1 -> FutureAppointmentsFragment()
        else -> MedicalSpecialtiesSearchFragment()
    }

    override fun getCount(): Int = pageTitles?.size ?: 0

    override fun getPageTitle(position: Int): CharSequence? =
        pageTitles?.let { it[position] } ?: super.getPageTitle(position)
}