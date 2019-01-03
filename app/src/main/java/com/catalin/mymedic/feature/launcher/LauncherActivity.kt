package com.catalin.mymedic.feature.launcher

import android.animation.ObjectAnimator
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.LauncherActivityBinding
import com.catalin.mymedic.feature.authentication.login.LoginActivity
import com.catalin.mymedic.feature.home.HomeActivity
import com.catalin.mymedic.storage.preference.SharedPreferencesManager
import com.catalin.mymedic.utils.extension.onPropertyChanged
import javax.inject.Inject

/**
 * Splash screen which decides which activity should be started next depending if the user was previously logged in or not
 *
 * @author catalinradoiu
 * @since 4/15/2018
 */
class LauncherActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory: LauncherActivityViewModel.LauncherActivityViewModelProvider

    @Inject
    lateinit var preferencesManager: SharedPreferencesManager

    private lateinit var viewModel: LauncherActivityViewModel

    private lateinit var binding: LauncherActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(LauncherActivityViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.launcher_activity)
        val currentUserWelcomeText =
            if (viewModel.currentUser != null && preferencesManager.currentUserName.isNotEmpty()) preferencesManager.currentUserName.split(" ")[0] else ""
        binding.launcherWelcomeText.text = if (currentUserWelcomeText.isEmpty()) getString(R.string.welcome_anonymous_user) else
            getString(R.string.welcome_authenticated_user, currentUserWelcomeText)
        viewModel.isAuthenticated.onPropertyChanged { isAuthenticated ->
            if (isAuthenticated) {
                startActivity(HomeActivity.getStartIntent(this))
            } else {
                startActivity(LoginActivity.getStartIntent(this))
            }
            overridePendingTransition(R.anim.appear, R.anim.disappear)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        startSplashAnimation()
    }

    private fun startSplashAnimation() {
        animateApplicationLogo()
        animateWelcomeText()
    }

    private fun animateApplicationLogo() {
        binding.root.post {
            ObjectAnimator.ofFloat(
                binding.launcherApplicationLogo, "y", (binding.root.height / 2 - binding.launcherApplicationLogo.measuredHeight).toFloat()
            ).apply {
                duration = WELCOME_ANIMATION_DURATION
                start()
            }
        }
    }

    private fun animateWelcomeText() {
        binding.root.post {
            ObjectAnimator.ofFloat(
                binding.launcherWelcomeText,
                "y",
                (binding.root.height / 2 - binding.launcherApplicationLogo.measuredHeight / 2 + resources.getDimensionPixelOffset(R.dimen.second_key_line)).toFloat()
            ).apply {
                duration = WELCOME_ANIMATION_DURATION
                start()
            }
        }
    }

    companion object {
        private const val WELCOME_ANIMATION_DURATION = 700L

        fun getStartIntent(context: Context) = Intent(context, LauncherActivity::class.java)
    }
}