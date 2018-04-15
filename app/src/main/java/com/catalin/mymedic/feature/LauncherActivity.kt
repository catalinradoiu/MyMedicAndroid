package com.catalin.mymedic.feature

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.feature.authentication.login.LoginActivity
import com.catalin.mymedic.feature.home.HomeActivity
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import javax.inject.Inject

/**
 * Splash screen which decides which activity should be started next depending if the user was previously logged in or not
 *
 * @author catalinradoiu
 * @since 4/15/2018
 */
class LauncherActivity : AppCompatActivity() {

    @Inject
    internal lateinit var preferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        if (preferencesManager.getCurrentUserEmail().isNotEmpty()) {
            startActivity(HomeActivity.getStartIntent(this))
        } else {
            startActivity(LoginActivity.getStartIntent(this))
        }
        finish()
    }

    override fun onResume() {
        super.onResume()
        startSplashAnimation()
    }

    private fun startSplashAnimation() {

    }
}