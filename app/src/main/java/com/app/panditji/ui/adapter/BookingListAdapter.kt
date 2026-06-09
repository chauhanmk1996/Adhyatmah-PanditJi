package com.app.panditji.ui.adapter

import IntroSlideData
import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.panditji.databinding.RcvItemBookingBinding
import com.app.panditji.utils.AppConstants
import com.app.panditji.utils.AppUtils
import com.bumptech.glide.Glide

class BookingListAdapter(
    private val context: Context?,
    var type: String,
    var setList: MutableList<GetBookingResponse.Payload.Booking>,
    var callBack: (Int, GetBookingResponse.Payload.Booking) -> Unit
)  : RecyclerView.Adapter<BookingListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RcvItemBookingBinding.inflate(
            (context as android.app.Activity).layoutInflater, parent, false)
        return ViewHolder(binding)
    }
    inner class ViewHolder(var binding: RcvItemBookingBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = setList[position]
        Log.i("TAG", "onBindViewHolder: "+data)
        holder.binding.apply {
            tvBookingId.text = data.bookingID
            btNameId.text = data.customer.firstName + " " + data.customer.lastName
            tvAddressId1.text = data.address.streetAddress + ", " + data.address.city + ", " + data.address.state + ", " + data.address.country + ", " + data.address.zip
            tvDate.text = AppUtils.formatDate(data.dateTime)
            poojaName.text = data.poojaType
            Glide.with(context!!).load(data.customer.image)
                .placeholder(R.drawable.pamdit_ji)
                .error(R.drawable.pamdit_ji)
                .into(ivProf)
        }

        if(type == AppConstants.PENDING){
            holder.binding.btStatusId.text = "Pending"
            holder.binding.btStatusId.backgroundTintList = context?.getColorStateList(R.color.yellow_e98347e5)
            holder.binding.btnCompleteId.visibility = android.view.View.GONE
            holder.binding.btnLayoutIdId.visibility = android.view.View.VISIBLE
        } else if(type == AppConstants.ON_GOING){
            holder.binding.btStatusId.backgroundTintList = context?.getColorStateList(R.color.green_00da45)
            holder.binding.btStatusId.text = "Ongoing"
            holder.binding.btnCompleteId.visibility = android.view.View.VISIBLE
            holder.binding.btnLayoutIdId.visibility = android.view.View.GONE
        } else if(type == AppConstants.UP_COMING){
            Log.i("TAG", "onBindViewHolder: upcoming "+"$data")
            holder.binding.btStatusId.text = "Upcoming"
            holder.binding.btStatusId.backgroundTintList = context?.getColorStateList(R.color.green_00da45)
            holder.binding.btnCompleteId.visibility = android.view.View.GONE
            holder.binding.btnLayoutIdId.visibility = android.view.View.GONE
            holder.binding.btnCancel.visibility = android.view.View.VISIBLE
        }else if(type == AppConstants.CANCELLED){
            holder.binding.btStatusId.text = "Cancelled"
            holder.binding.btStatusId.backgroundTintList = context?.getColorStateList(R.color.red_e30000)
            holder.binding.btnCompleteId.visibility = android.view.View.GONE
            holder.binding.btnLayoutIdId.visibility = android.view.View.GONE
            holder.binding.btnCancel.visibility = android.view.View.GONE
        }
        else{
            holder.binding.btStatusId.backgroundTintList = context?.getColorStateList(R.color.green_00da45)
            holder.binding.btStatusId.text = "Completed"
            holder.binding.btnCompleteId.visibility = android.view.View.GONE
            holder.binding.btnLayoutIdId.visibility = android.view.View.GONE
        }
        holder.binding.cardUpcomingContainer.setOnClickListener{
            callBack(0, data)

        }
        holder.binding.acceptIdBtn.setOnClickListener{
            callBack(1, data)

        }
        holder.binding.declineIdBtn.setOnClickListener{
            callBack(2, data)

        }
        holder.binding.btnCancel.setOnClickListener{
            callBack(3, data)

        }
        holder.binding.btnCompleteId.setOnClickListener{
            callBack(4, data)

        }
    }
    fun updateData(newList: List<GetBookingResponse.Payload.Booking>) {
        setList.clear()
        setList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return setList.size
    }


}