package com.app.panditji.ui.adapter

import IntroSlideData
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.panditji.databinding.ItemIntroPageLayoutBinding

class IntroAdapter(
    private val introList: List<IntroSlideData>,
    private var context: Context
) :
    RecyclerView.Adapter<IntroAdapter.IntroViewHolder>() {

    inner class IntroViewHolder(private val binding: ItemIntroPageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: IntroSlideData) {
            binding.titleIntro.text = item.title
            binding.descIntro.text = item.description
            Glide.with(context)
                .load(item.imageResId)
                .into(binding.imageIntro)
           // binding.imageIntro.setImageResource(item.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        val binding = ItemIntroPageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IntroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        holder.bind(introList[position])
    }

    override fun getItemCount(): Int = introList.size
}
