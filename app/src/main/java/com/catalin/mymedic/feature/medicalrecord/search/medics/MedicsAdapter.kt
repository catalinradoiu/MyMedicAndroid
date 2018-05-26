package com.catalin.mymedic.feature.medicalrecord.search.medics

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.catalin.mymedic.data.User

/**
 * @author catalinradoiu
 * @since 5/26/2018
 */
class MedicsAdapter : RecyclerView.Adapter<MedicsAdapter.MedicViewHolder>() {

    private val medicsList = ArrayList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int = medicsList.size

    override fun onBindViewHolder(holder: MedicViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class MedicViewHolder() : RecyclerView.ViewHolder(null) {

        fun bind(medic: User) {

        }
    }
}