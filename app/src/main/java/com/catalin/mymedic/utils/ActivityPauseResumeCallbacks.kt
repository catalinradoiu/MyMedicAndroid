package com.catalin.mymedic.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * @author catalinradoiu
 * @since 6/22/2018
 */
abstract class ActivityPauseResumeCallbacks : Application.ActivityLifecycleCallbacks {

    override fun onActivityStarted(activity: Activity?) = Unit

    override fun onActivityDestroyed(activity: Activity?) = Unit

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) = Unit

    override fun onActivityStopped(activity: Activity?) = Unit

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) = Unit
}