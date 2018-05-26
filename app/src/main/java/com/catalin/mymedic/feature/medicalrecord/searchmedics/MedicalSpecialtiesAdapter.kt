package com.catalin.mymedic.feature.medicalrecord.searchmedics

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.catalin.mymedic.data.MedicalSpecialty
import com.catalin.mymedic.databinding.MedicalSpecialtyItemBinding

/**
 * @author catalinradoiu
 * @since 5/25/2018
 */
class MedicalSpecialtiesAdapter :
    RecyclerView.Adapter<MedicalSpecialtiesAdapter.MedicalSpecialtyViewHolder>() {

    private val medicalSpecialties = ArrayList<MedicalSpecialty>()
    private var onItemClickListener: OnItemClickListener? = null

    init {

    }

    fun addMedicalSpecialties(medicalSpecialties: List<MedicalSpecialty>) {
        this.medicalSpecialties.addAll(medicalSpecialties)
        notifyDataSetChanged()
    }

    fun getMedicalSpecialty(position: Int) = medicalSpecialties[position]

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
        holder.bind(medicalSpecialties[position])
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    class MedicalSpecialtyViewHolder(
        private val binding: MedicalSpecialtyItemBinding,
        private val onItemClickListener: OnItemClickListener?
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(medicalSpecialty: MedicalSpecialty) {
            binding.specialtyName.text = medicalSpecialty.name
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onItemClick(position)
                }
            }
        }
    }
}