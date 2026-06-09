package com.app.panditji.ui.adapter

import IntroSlideData
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.databinding.RcvItemBookingBinding
import com.app.panditji.utils.AppConstants

class PreviousBookingAdapter(
    private val context: Context?,
    var type: String,
    var setList: MutableList<IntroSlideData>,
    var callBack: (Int) -> Unit
) : RecyclerView.Adapter<PreviousBookingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RcvItemBookingBinding.inflate(
            (context as android.app.Activity).layoutInflater, parent, false)
        return ViewHolder(binding)
    }
    inner class ViewHolder(var binding: RcvItemBookingBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = setList[position]


        if(type == AppConstants.PENDING){
            holder.binding.btStatusId.text = "Pending"
            holder.binding.btStatusId.backgroundTintList = context?.getColorStateList(com.app.panditji.R.color.red_e30000)
            holder.binding.btnCompleteId.visibility = android.view.View.GONE
            holder.binding.btnLayoutIdId.visibility = android.view.View.VISIBLE
       } else if(type == AppConstants.ON_GOING){
            holder.binding.btStatusId.backgroundTintList = context?.getColorStateList(com.app.panditji.R.color.yellow_e98347e5)
            holder.binding.btStatusId.text = "Ongoing"
            holder.binding.btnCompleteId.visibility = android.view.View.VISIBLE
            holder.binding.btnLayoutIdId.visibility = android.view.View.GONE
        } else if(type == AppConstants.UP_COMING){
            holder.binding.btStatusId.text = "Upcoming"
            holder.binding.btStatusId.backgroundTintList = context?.getColorStateList(com.app.panditji.R.color.green_00da45)
            holder.binding.btnCompleteId.visibility = android.view.View.GONE
            holder.binding.btnLayoutIdId.visibility = android.view.View.GONE
        }
        else{
            holder.binding.btStatusId.text = "Completed"
            holder.binding.btnCompleteId.visibility = android.view.View.GONE
            holder.binding.btnLayoutIdId.visibility = android.view.View.GONE
        }
        holder.binding.cardUpcomingContainer.setOnClickListener{
            callBack(position)

        }
    }

    override fun getItemCount(): Int {
        return setList.size
    }


}






