package com.app.panditji.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.databinding.ProfileItemContainerBinding

class ProfileAdapter(
        private val profileItems: List<Pair<String, Int>>,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

        inner class ProfileViewHolder(private val binding: ProfileItemContainerBinding) :
            RecyclerView.ViewHolder(binding.root) {
//            var isLogin = Preferences.getStringPreference(binding.view.context, IS_LOGIN).toString()=="1"
            fun bind(item: Pair<String, Int>, isLastItem: Boolean) {
                val context = itemView.context

            binding.textView.text = item.first
            binding.contactImage.setImageResource(item.second)

       binding.root.setOnClickListener {
                    onItemClick(item.first)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
            val binding = ProfileItemContainerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return ProfileViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
            val isLastItem = position == profileItems.size - 1

            holder.bind(profileItems[position],isLastItem)

        }

        override fun getItemCount(): Int = profileItems.size

}