package com.catalin.mymedic.feature.home

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.HomeActivityBinding

/**
 * @author catalinradoiu
 * @since 4/15/2018
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.home_activity)
    }

    companion object {
        fun getStartIntent(context: Context) = Intent(context, HomeActivity::class.java)
    }
}