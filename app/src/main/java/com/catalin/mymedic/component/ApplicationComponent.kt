package com.catalin.mymedic.component

import com.catalin.mymedic.feature.authentication.login.LoginActivity
import com.catalin.mymedic.feature.authentication.registration.RegistrationActivity
import dagger.Component
import javax.inject.Singleton

/**
 * @author catalinradoiu
 * @since 2/6/2018
 */

@Singleton
@Component(modules = [(FirebaseModule::class)])
interface ApplicationComponent {

    fun inject(registrationActivity: RegistrationActivity)
    fun inject(registrationActivity: LoginActivity)
}
