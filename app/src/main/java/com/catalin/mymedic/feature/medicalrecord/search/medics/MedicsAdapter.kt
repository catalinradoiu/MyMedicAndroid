package com.catalin.mymedic.feature.medicalrecord.search.medics

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.catalin.mymedic.data.User
import com.catalin.mymedic.databinding.MedicSearchItemBinding
import com.catalin.mymedic.utils.GlideApp
import com.google.firebase.storage.FirebaseStorage

/**
 * @author catalinradoiu
 * @since 5/26/2018
 */
class MedicsAdapter(private val firebaseStorage: FirebaseStorage) : RecyclerView.Adapter<MedicsAdapter.MedicViewHolder>() {

    private val medicsList = ArrayList<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicViewHolder =
        MedicViewHolder(MedicSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = medicsList.size

    override fun onBindViewHolder(holder: MedicViewHolder, position: Int) {
        holder.bind(medicsList[position], firebaseStorage)
    }

    fun setMedics(medicsList: List<User>) {
        this.medicsList.addAll(medicsList)
        notifyDataSetChanged()
    }

    class MedicViewHolder(private val binding: MedicSearchItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(medic: User, firebaseStorage: FirebaseStorage) {
            binding.medicName.text = medic.displayName
            GlideApp.with(itemView).load(firebaseStorage.reference.child(medic.imageUrl)).into(binding.medicPhoto)
        }
    }
}