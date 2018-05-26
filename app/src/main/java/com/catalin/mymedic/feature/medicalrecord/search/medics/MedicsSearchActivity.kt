package com.catalin.mymedic.feature.medicalrecord.search.medics

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.databinding.MedicsSearchActivityBinding
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/26/2018
 */
class MedicsSearchActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: MedicsSearchViewModel.Factory

    private lateinit var binding: MedicsSearchActivityBinding
    private lateinit var viewModel: MedicsSearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MedicsSearchViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.medics_search_activity)
        supportActionBar?.let {
            title = intent.getStringExtra(SPECIALTY_NAME)
        }
        viewModel.initMedicsList(intent.getIntExtra(SPECIALTY_ID, 0))
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