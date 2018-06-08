package com.catalin.mymedic.storage.repository

import com.catalin.mymedic.data.MedicDetails
import com.catalin.mymedic.storage.source.MedicDetailsFirebaseSource
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author catalinradoiu
 * @since 6/1/2018
 */
@Singleton
class MedicDetailsRepository @Inject constructor(private var medicsDetailsFirebaseSource: MedicDetailsFirebaseSource) {

    private val medicsDetailsList = ArrayList<MedicDetails>()

    fun getDetailsForMedic(medicId: String): Single<MedicDetails> =
        medicsDetailsFirebaseSource.getDetailsForMedic(medicId).doOnSuccess { details -> medicsDetailsList.add(details) }
}