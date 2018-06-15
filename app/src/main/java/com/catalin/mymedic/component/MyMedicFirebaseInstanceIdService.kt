package com.catalin.mymedic.component

import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.storage.source.UsersFirebaseSource
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 6/8/2018
 */
class MyMedicFirebaseInstanceIdService : FirebaseInstanceIdService() {

    @Inject
    lateinit var usersFirebaseSource: UsersFirebaseSource

    @Inject
    lateinit var preferencesManager: SharedPreferencesManager

    override fun onCreate() {
        super.onCreate()
        (application as MyMedicApplication).applicationComponent.inject(this)
    }

    override fun onTokenRefresh() {
        //Get updated instance id token
        val refreshedToken = FirebaseInstanceId.getInstance().token

        usersFirebaseSource.updateUserNotificationToken(refreshedToken, preferencesManager.currentUserId)
    }
}