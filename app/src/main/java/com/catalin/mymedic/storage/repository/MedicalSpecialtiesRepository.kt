package com.catalin.mymedic.storage.repository

import com.catalin.mymedic.data.MedicalSpecialty
import com.catalin.mymedic.storage.source.MedicalSpecialtiesFirebaseSource
import io.reactivex.Single
import javax.inject.Inject

/**
 * @author catalinradoiu
 * @since 5/23/2018
 */
class MedicalSpecialtiesRepository @Inject constructor(private val medicalSpecialtiesSource: MedicalSpecialtiesFirebaseSource) {

    private val medicalSpecialties = ArrayList<MedicalSpecialty>()

    fun getAllMedicalSpecialties(): Single<List<MedicalSpecialty>> = if (medicalSpecialties.isEmpty())
        medicalSpecialtiesSource.getAllSpecialties().doOnSuccess { elements -> medicalSpecialties.addAll(elements) } else Single.just(medicalSpecialties)
}