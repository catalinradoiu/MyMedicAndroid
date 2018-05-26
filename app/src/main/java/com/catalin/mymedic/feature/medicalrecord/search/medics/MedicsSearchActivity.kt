package com.catalin.mymedic.feature.medicalrecord.search.medics

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.MedicsSearchActivityBinding

/**
 * @author catalinradoiu
 * @since 5/26/2018
 */
class MedicsSearchActivity : AppCompatActivity() {

    private lateinit var binding: MedicsSearchActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.medics_search_activity)
        supportActionBar?.let {
            title = intent.getStringExtra(SPECIALTY_NAME)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        private const val SPECIALTY_ID = "specialtyId"
        private const val SPECIALTY_NAME = "specialtyName"

        fun getStartIntent(context: Context, specialtyId: Int, specialtyName: String): Intent = Intent(context, MedicsSearchActivity::class.java)
            .putExtra(SPECIALTY_NAME, specialtyName).putExtra(SPECIALTY_ID, specialtyId)
    }
}