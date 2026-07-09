package com.app.panditji.ui.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.data.model.SignUpData
import com.app.panditji.databinding.ItemLanguageBinding

class ExperienceAdapter(
    private val experienceList: List<SignUpData>,
    private val selectedExperience: MutableList<SignUpData>,
) : RecyclerView.Adapter<ExperienceAdapter.ServiceViewHolder>() {

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

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val experience = experienceList[position]

        val isSelected = selectedExperience.any {
            it.name == experience.name
        }

        holder.binding.tvLanguage.text = experience.name

        val drawableRes = if (isSelected) {
            R.drawable.radio_button_checked
        } else {
            R.drawable.radio_button_unchecked
        }

        holder.binding.tvLanguage.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0)

        holder.binding.tvLanguage.setOnClickListener {
            selectedExperience.clear()
            selectedExperience.add(experience)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int = experienceList.size
}