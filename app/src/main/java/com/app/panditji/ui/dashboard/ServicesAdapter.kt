package com.app.panditji.ui.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.data.model.get_services.GetAllServicesResponse
import com.app.panditji.databinding.ItemLanguageBinding

class ServicesAdapter(
    private val languages: List<GetAllServicesResponse.Payload.Service>,
    private val selectedLanguages: MutableList<GetAllServicesResponse.Payload.Service>
) : RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder>() {

    inner class ServiceViewHolder(val binding: ItemLanguageBinding) :
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
        position: Int
    ) {
        val language = languages[position]

        val isSelected = selectedLanguages.any {
            Log.i("TAG", "onBindViewHolder: ${it.poojaType}  ${language.poojaType}")
            it.poojaType == language.poojaType
        }

        holder.binding.tvLanguage.text = languages[position].poojaType

        // Update checked/unchecked drawable dynamically
        val drawableRes = if (isSelected)
            R.drawable.radio_button_checked
        else
            R.drawable.radio_button_unchecked

        holder.binding.tvLanguage.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0)

        holder.binding.tvLanguage.setOnClickListener {
            val currentlySelected = selectedLanguages.any { it.poojaType == language.poojaType }

            if (currentlySelected) {
                selectedLanguages.removeAll { it.poojaType == language.poojaType }
            } else {
                selectedLanguages.add(language)
            }

            notifyItemChanged(position)
        }
    }


    override fun getItemCount(): Int = languages.size
}