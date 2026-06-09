package com.app.panditji.ui.adapter

import IntroSlideData
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.data.model.get_banner.GetBannerResponse.Payload.Slide
import com.app.panditji.databinding.ItemViewpagerImageLayoutBinding
import com.bumptech.glide.Glide


class BannerAdapter(
    private var introList: List<Slide>
//      private var introList: List<Banner>
) :
    RecyclerView.Adapter<BannerAdapter.IntroViewHolder>() {

    inner class IntroViewHolder(private val binding: ItemViewpagerImageLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Slide) {

            val imageUrl = item.image.url
//            val imageUrl = item.imageResId

            if (imageUrl.isNotBlank()) {
                Glide.with(itemView.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.banner_img)
                    .error(R.drawable.banner_img)
                    .into(binding.ivViewPager)
            } else {
                // Load default banner manually
                Glide.with(itemView.context)
                    .load(R.drawable.banner_img)
                    .into(binding.ivViewPager)
            }

//            binding.ivViewPager.setImageResource(item.imageResId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        val binding = ItemViewpagerImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IntroViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        holder.bind(introList[position])
    }

    override fun getItemCount(): Int = introList.size

//    fun setItems(newBanners: List<IntroSlideData>) {
////        introList = newBanners
//        introList = newBanners
//        notifyDataSetChanged()
//    }

}
