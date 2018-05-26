package com.catalin.mymedic.feature.medicalrecord.search.specialties

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.databinding.ObservableField
import com.catalin.mymedic.data.MedicalSpecialty
import com.catalin.mymedic.storage.repository.MedicalSpecialtiesRepository
import com.catalin.mymedic.utils.OperationResult
import com.catalin.mymedic.utils.extension.mainThreadSubscribe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Consumer
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/23/2018
 */
class MedicalSpecialtiesSearchViewModel(private val medicalSpecialtiesRepository: MedicalSpecialtiesRepository) :
    ViewModel() {

    val medicalSpecialtiesList = ObservableField<List<MedicalSpecialty>>()
    val operationResult = ObservableField<OperationResult>(OperationResult.NoOperation)

    private val disposables = CompositeDisposable()

    fun initMedicalSpecialties() {
        medicalSpecialtiesRepository.getAllMedicalSpecialties().mainThreadSubscribe(
            Consumer {
                medicalSpecialtiesList.set(it)
            },
            Consumer {
                operationResult.set(OperationResult.Error(it.message))
            }
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    class MedicalSpecialtiesViewModelFactory @Inject constructor(
        private val medicalSpecialtiesRepository: MedicalSpecialtiesRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T =
            MedicalSpecialtiesSearchViewModel(medicalSpecialtiesRepository) as T
    }


}