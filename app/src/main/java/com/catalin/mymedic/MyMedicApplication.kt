package com.catalin.mymedic

import android.app.Application
import com.catalin.mymedic.component.ApplicationComponent
import com.catalin.mymedic.component.DaggerApplicationComponent

/**
 * @author catalinradoiu
 * @since 2/6/2018
 */
class MyMedicApplication : Application() {

    val applicationComponent: ApplicationComponent = DaggerApplicationComponent.builder().build()
}