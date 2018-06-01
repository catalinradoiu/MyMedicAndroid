package com.catalin.mymedic.feature.medicalrecord.search.medics

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.catalin.mymedic.MyMedicApplication
import com.catalin.mymedic.R
import com.catalin.mymedic.data.User
import com.catalin.mymedic.databinding.MedicsSearchActivityBinding
import com.catalin.mymedic.feature.createappointment.AppointmentCreateActivity
import com.catalin.mymedic.feature.shared.OnTextChangedListener
import com.catalin.mymedic.utils.extension.dismissIfVisible
import com.catalin.mymedic.utils.extension.newLongSnackbar
import com.catalin.mymedic.utils.extension.onPropertyChanged
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/26/2018
 */
class MedicsSearchActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: MedicsSearchViewModel.Factory

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private lateinit var binding: MedicsSearchActivityBinding
    private lateinit var viewModel: MedicsSearchViewModel
    private lateinit var medicsAdapter: MedicsAdapter
    private var operationSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyMedicApplication).applicationComponent.inject(this)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MedicsSearchViewModel::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.medics_search_activity)
        binding.viewModel = viewModel

        binding.filteringProgressBar.indeterminateDrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        setSupportActionBar(binding.toolbar.apply {
            title = intent.getStringExtra(SPECIALTY_NAME)
        })
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        medicsAdapter = MedicsAdapter(firebaseStorage)
        binding.medicsRecycler.adapter = medicsAdapter
        binding.medicsRecycler.layoutManager = LinearLayoutManager(this)

        viewModel.getMedicsList(intent.getIntExtra(SPECIALTY_ID, NO_SPECIALTY_ID))
        initListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStop() {
        super.onStop()
        operationSnackbar?.dismissIfVisible()
    }

    private fun initListeners() {
        viewModel.medicsList.observe(this, Observer<List<User>> { medics ->
            medics?.let {
                medicsAdapter.setMedics(medics)
            }
        })

        viewModel.isError.onPropertyChanged { isError ->
            if (isError) {
                displaySnackBar(getString(R.string.could_not_retrieve_medics))
            }
        }

        binding.searchInput.addTextChangedListener(object : OnTextChangedListener() {
            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.getFilteredMedics(intent.getIntExtra(SPECIALTY_ID, NO_SPECIALTY_ID))
            }

        })

        medicsAdapter.setOnItemClickListener(object : MedicsAdapter.OnItemClickListener {
            override fun onNewAppointmentClick(position: Int) {
                val medic = medicsAdapter.getMedic(position)
                startActivity(
                    AppointmentCreateActivity.getStartIntent(
                        this@MedicsSearchActivity,
                        medic.displayName,
                        intent.getStringExtra(SPECIALTY_NAME),
                        medic.id
                    )
                )
            }

            override fun onNewMessageClick(position: Int) {

            }

        })
    }

    private fun displaySnackBar(message: String) {
        operationSnackbar?.newLongSnackbar(binding.root, message)
    }

    companion object {
        private const val SPECIALTY_ID = "specialtyId"
        private const val SPECIALTY_NAME = "specialtyName"
        private const val NO_SPECIALTY_ID = 0

        fun getStartIntent(context: Context, specialtyId: Int, specialtyName: String): Intent = Intent(context, MedicsSearchActivity::class.java)
            .putExtra(SPECIALTY_NAME, specialtyName).putExtra(SPECIALTY_ID, specialtyId)
    }
}