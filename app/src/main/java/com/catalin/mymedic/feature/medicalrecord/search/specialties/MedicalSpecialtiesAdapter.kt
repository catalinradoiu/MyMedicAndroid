package com.catalin.mymedic.feature.medicalrecord.search.specialties

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.catalin.mymedic.data.MedicalSpecialty
import com.catalin.mymedic.databinding.MedicalSpecialtyItemBinding
import com.catalin.mymedic.utils.GlideApp
import com.google.firebase.storage.FirebaseStorage

/**
 * @author catalinradoiu
 * @since 5/25/2018
 */
class MedicalSpecialtiesAdapter(private val firebaseStorage: FirebaseStorage) :
    RecyclerView.Adapter<MedicalSpecialtiesAdapter.MedicalSpecialtyViewHolder>() {

    private val medicalSpecialties = ArrayList<MedicalSpecialty>()
    private var onItemClickListener: OnItemClickListener? = null

    fun addMedicalSpecialties(medicalSpecialties: List<MedicalSpecialty>) {
        this.medicalSpecialties.addAll(medicalSpecialties)
        notifyDataSetChanged()
    }

    fun getMedicalSpecialty(position: Int) = medicalSpecialties[position]

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicalSpecialtyViewHolder =
        MedicalSpecialtyViewHolder(
            MedicalSpecialtyItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onItemClickListener
        )

    override fun getItemCount(): Int = medicalSpecialties.size

    override fun onBindViewHolder(holder: MedicalSpecialtyViewHolder, position: Int) {
        holder.bind(medicalSpecialties[position], firebaseStorage)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class MedicalSpecialtyViewHolder(
        private val binding: MedicalSpecialtyItemBinding,
        private val onItemClickListener: OnItemClickListener?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(medicalSpecialty: MedicalSpecialty, firebaseStorage: FirebaseStorage) {
            binding.specialtyName.text = medicalSpecialty.name
            GlideApp.with(itemView.context).load(firebaseStorage.reference.child(medicalSpecialty.imageUrl)).into(binding.specialtyImage)
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onItemClick(position)
                }
            }
        }
    }
}