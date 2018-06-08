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

    private var medicsList = ArrayList<User>()
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicViewHolder =
        MedicViewHolder(MedicSearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false), onItemClickListener)

    override fun getItemCount(): Int = medicsList.size

    override fun onBindViewHolder(holder: MedicViewHolder, position: Int) {
        holder.bind(medicsList[position], firebaseStorage)
    }

    fun setMedics(medicsList: List<User>) {
        this.medicsList = ArrayList(medicsList)
        notifyDataSetChanged()
    }

    fun getMedic(position: Int) = medicsList[position]

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onNewAppointmentClick(position: Int)
        fun onNewMessageClick(position: Int)
    }

    class MedicViewHolder(private val binding: MedicSearchItemBinding, private val onItemClickListener: OnItemClickListener?) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(medic: User, firebaseStorage: FirebaseStorage) {
            GlideApp.with(itemView).load(firebaseStorage.reference.child(medic.imageUrl)).into(binding.medicPhoto)
            binding.medicName.text = medic.displayName
            binding.newAppointmentButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onNewAppointmentClick(position)
                }
            }
            binding.newMessageButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onNewMessageClick(position)
                }
            }
        }
    }
}