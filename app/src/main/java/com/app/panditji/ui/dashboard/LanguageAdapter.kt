package com.app.panditji.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.databinding.ItemLanguageBinding

class LanguageAdapter(
    private val languages: List<String>,
    private val selectedLanguages: MutableList<String>
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    inner class LanguageViewHolder(val binding: ItemLanguageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = ItemLanguageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]
        val isSelected = selectedLanguages.contains(language)

        holder.binding.tvLanguage.text = language

        // Update checked/unchecked drawable dynamically
        val drawableRes = if (isSelected)
            R.drawable.radio_button_checked
        else
            R.drawable.radio_button_unchecked

        holder.binding.tvLanguage.setCompoundDrawablesWithIntrinsicBounds(0, 0, drawableRes, 0)

        holder.binding.tvLanguage.setOnClickListener {
            if (isSelected) {
                selectedLanguages.remove(language)
            } else {
                selectedLanguages.add(language)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = languages.size
}
