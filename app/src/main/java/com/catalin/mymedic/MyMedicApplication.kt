package com.catalin.mymedic

import android.app.Application
import com.catalin.mymedic.architecture.ApplicationComponent
import com.catalin.mymedic.architecture.ApplicationModule
import com.catalin.mymedic.architecture.DaggerApplicationComponent
import com.google.firebase.database.FirebaseDatabase

/**
 * Application class
 * Takes care of the dagger components initialisation
 *
 * @author catalinradoiu
 * @since 2/6/2018
 */
class MyMedicApplication : Application() {

    val applicationComponent: ApplicationComponent = DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()

    override fun onCreate() {
        super.onCreate()
        val database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)
        database.getReference("users").keepSynced(true)
    }
}