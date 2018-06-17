package com.catalin.mymedic.architecture

import com.catalin.mymedic.component.MyMedicFirebaseInstanceIdService
import com.catalin.mymedic.feature.authentication.login.LoginActivity
import com.catalin.mymedic.feature.authentication.passwordreset.PasswordResetActivity
import com.catalin.mymedic.feature.authentication.registration.RegistrationActivity
import com.catalin.mymedic.feature.createappointment.AppointmentCreateActivity
import com.catalin.mymedic.feature.launcher.LauncherActivity
import com.catalin.mymedic.feature.medicalrecord.MedicalRecordFragment
import com.catalin.mymedic.feature.medicalrecord.awaitingappointments.AwaitingAppointmentsFragment
import com.catalin.mymedic.feature.medicalrecord.search.medics.MedicsSearchActivity
import com.catalin.mymedic.feature.medicalrecord.search.specialties.MedicalSpecialtiesSearchFragment
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
    fun inject(awaitingAppointmentsFragment: AwaitingAppointmentsFragment)
}
