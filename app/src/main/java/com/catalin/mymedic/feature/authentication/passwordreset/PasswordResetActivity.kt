package com.catalin.mymedic.feature.authentication.passwordreset

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.PasswordResetActivityBinding
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/10/2018
 */
class PasswordResetActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: PasswordResetViewModel

    private lateinit var binding: PasswordResetActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.password_reset_activity)
        binding.viewModel = viewModel
    }
}