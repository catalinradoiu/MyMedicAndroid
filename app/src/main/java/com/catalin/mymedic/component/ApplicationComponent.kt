package com.catalin.mymedic.component

import com.catalin.mymedic.feature.authentication.RegistrationActivity
import dagger.Component
import javax.inject.Singleton

/**
 * @author catalinradoiu
 * @since 2/6/2018
 */

@Singleton
@Component(modules = [AppModule::class])
interface ApplicationComponent{

    fun inject(registrationActivity: RegistrationActivity)
}
