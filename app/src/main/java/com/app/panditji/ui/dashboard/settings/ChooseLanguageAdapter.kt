package com.app.panditji.ui.dashboard.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.data.model.app_language.AppLanguage
import com.app.panditji.databinding.RecyclerMultiplePoojaBinding
import java.util.Locale

class ChooseLanguageAdapter(
    private val languages: List<AppLanguage>,
    private var selectedLanguageCode: String?,
    private val onSelect: (AppLanguage) -> Unit
) : RecyclerView.Adapter<ChooseLanguageAdapter.LanguageViewHolder>() {

    inner class LanguageViewHolder(val binding: RecyclerMultiplePoojaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val binding = RecyclerMultiplePoojaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val language = languages[position]

        holder.binding.textView.text = language.name

        val isSelected = language.code == selectedLanguageCode

        val drawableRes = if (isSelected)
            R.drawable.selected_round_btn
        else
            R.drawable.unselected_rounded_btn

        holder.binding.textView.setCompoundDrawablesWithIntrinsicBounds(
            0, 0, drawableRes, 0
        )

        holder.binding.textView.setOnClickListener {

            selectedLanguageCode = language.code
            notifyDataSetChanged()

            onSelect(language)
        }
    }
    override fun getItemCount(): Int = languages.size
}
