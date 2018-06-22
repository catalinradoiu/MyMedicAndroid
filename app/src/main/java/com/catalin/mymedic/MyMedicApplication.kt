package com.catalin.mymedic

import android.app.Application
import com.catalin.mymedic.architecture.ApplicationComponent
import com.catalin.mymedic.architecture.ApplicationModule
import com.catalin.mymedic.architecture.DaggerApplicationComponent
import com.catalin.mymedic.utils.FirebaseDatabaseConfig
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Application class
 * Takes care of the dagger components initialisation
 *
 * @author catalinradoiu
 * @since 2/6/2018
 */
class MyMedicApplication : Application() {

    //TODO : Add flag to check when the app is in foreground in order to not display notifications in that case

    val applicationComponent: ApplicationComponent = DaggerApplicationComponent.builder().applicationModule(ApplicationModule(this)).build()

    override fun onCreate() {
        super.onCreate()
        val database = FirebaseDatabase.getInstance()
        database.setPersistenceEnabled(true)
        database.reference.child(FirebaseDatabaseConfig.MEDICAL_APPOINTMENTS_TABLE_NAME).keepSynced(true)
        database.reference.child(FirebaseDatabaseConfig.CONVERSATIONS).keepSynced(true)
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
    }
}