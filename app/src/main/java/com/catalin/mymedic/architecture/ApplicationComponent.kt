package com.catalin.mymedic.architecture

import com.catalin.mymedic.component.MyMedicFirebaseInstanceIdService
import com.catalin.mymedic.component.MyMedicFirebaseMessagingService
import com.catalin.mymedic.feature.appointmentdetails.AppointmentDetailsActivity
import com.catalin.mymedic.feature.authentication.login.LoginActivity
import com.catalin.mymedic.feature.authentication.passwordreset.PasswordResetActivity
import com.catalin.mymedic.feature.authentication.registration.RegistrationActivity
import com.catalin.mymedic.feature.chat.ConversationsListFragment
import com.catalin.mymedic.feature.chat.conversationdetails.ConversationDetailsActivity
import com.catalin.mymedic.feature.createappointment.AppointmentCreateActivity
import com.catalin.mymedic.feature.launcher.LauncherActivity
import com.catalin.mymedic.feature.medicalrecord.MedicalRecordFragment
import com.catalin.mymedic.feature.medicalrecord.awaitingappointments.AwaitingAppointmentsFragment
import com.catalin.mymedic.feature.medicalrecord.futureappointments.FutureAppointmentsFragment
import com.catalin.mymedic.feature.medicalrecord.medicalhistory.MedicalHistoryFragment
import com.catalin.mymedic.feature.medicalrecord.search.medics.MedicsSearchActivity
import com.catalin.mymedic.feature.medicalrecord.search.specialties.MedicalSpecialtiesSearchFragment
import com.catalin.mymedic.feature.profile.ProfileFragment
import com.catalin.mymedic.feature.profile.edit.ProfileEditActivity
import com.catalin.mymedic.feature.settings.SettingsFragment
import dagger.Component
import javax.inject.Singleton

/**
 * @author catalinradoiu
 * @since 2/6/2018
 */

@Singleton
@Component(modules = [FirebaseModule::class, ApplicationModule::class])
interface ApplicationComponent {

    fun inject(registrationActivity: RegistrationActivity)
    fun inject(registrationActivity: LoginActivity)
    fun inject(launcherActivity: LauncherActivity)
    fun inject(medicalRecordFragment: MedicalRecordFragment)
    fun inject(passwordResetActivity: PasswordResetActivity)
    fun inject(medicalSpecialtiesSearchFragment: MedicalSpecialtiesSearchFragment)
    fun inject(medicsSearchActivity: MedicsSearchActivity)
    fun inject(appointmentCreateActivity: AppointmentCreateActivity)
    fun inject(firebaseInstanceIdService: MyMedicFirebaseInstanceIdService)
    fun inject(futureAppointmentsFragment: FutureAppointmentsFragment)
    fun inject(awaitingAppointmentsFragment: AwaitingAppointmentsFragment)
    fun inject(conversationsListFragment: ConversationsListFragment)
    fun inject(conversationDetailsActivity: ConversationDetailsActivity)
    fun inject(profileFragment: ProfileFragment)
    fun inject(profileEditActivity: ProfileEditActivity)
    fun inject(settingsFragment: SettingsFragment)
    fun inject(myMedicFirebaseMessagingService: MyMedicFirebaseMessagingService)
    fun inject(medicalHistoryFragment: MedicalHistoryFragment)
    fun inject(appointmentDetailsActivity: AppointmentDetailsActivity)
}
