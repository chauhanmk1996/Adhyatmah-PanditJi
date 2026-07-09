package com.app.panditji.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.data.model.SignUpData
import com.app.panditji.databinding.ItemLanguageBinding

class DesignationAdapter(
    private val designationList: List<SignUpData>,
    private val selectedDesignation: MutableList<SignUpData>,
) : RecyclerView.Adapter<DesignationAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemLanguageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ServiceViewHolder,
        position: Int,
    ) {
        val designation = designationList[position]

        val isSelected = selectedDesignation.any {
            it.name == designation.name
        }

        holder.binding.tvLanguage.text = designation.name

        val drawableRes = if (isSelected)
            R.drawable.radio_button_checked
        else
            R.drawable.radio_button_unchecked

        holder.binding.tvLanguage.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0)

        holder.binding.tvLanguage.setOnClickListener {
            val currentlySelected = selectedDesignation.any { it.name == designation.name }

            if (currentlySelected) {
                selectedDesignation.removeAll { it.name == designation.name }
            } else {
                selectedDesignation.add(designation)
            }

            notifyItemChanged(position)
        }
    }


    override fun getItemCount(): Int = designationList.size
}