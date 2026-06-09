package com.app.panditji.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.data.model.faq.Payload
import com.app.panditji.R
import com.app.panditji.databinding.AdapterFaqBinding


class AdapterFaq(var faqList: List<Payload>) : RecyclerView.Adapter<AdapterFaq.ViewHolder>() {
    var selectedPosition = -1

    inner class ViewHolder(itemView: AdapterFaqBinding) : RecyclerView.ViewHolder(itemView.root){
        var binding: AdapterFaqBinding = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.adapter_faq, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.mainLayout.isFocusable = true
        holder.binding.mainLayout.isClickable = true
        holder.binding.title.text = faqList[position].question
        holder.binding.description.text = faqList[position].answer
        /* val color = if (Preferences.getStringPreference(holder.itemView.context, ROLE)=="2") {
             R.color.theme
         } else {
             R.color.red_theme
         }
 */
        /* holder.binding.expandButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, color), PorterDuff.Mode.SRC_IN)
 */
        if (position != selectedPosition) {
            holder.binding.description.visibility = View.GONE
            holder.binding.expandButton.setImageResource(R.drawable.faq_plus)
            holder.binding.view.visibility = View.GONE
        }else{
            holder.binding.description.visibility = View.VISIBLE
            holder.binding.expandButton.setImageResource(R.drawable.faq_minus)
            holder.binding.view.visibility = View.VISIBLE
        }
        holder.binding.mainLayout.setOnClickListener {
            if (selectedPosition == position) {
                selectedPosition = -1
                notifyItemChanged(position)
            } else {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition) // Collapse old item
                notifyItemChanged(position) // Expand new item
            }
        }

    }

    override fun getItemCount(): Int {
        return faqList.size
    }
}