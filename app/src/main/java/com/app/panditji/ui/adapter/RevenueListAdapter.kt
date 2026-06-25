package com.app.panditji.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.data.model.revenue_list.GetRevenueListResponse.Payload.RecentBooking
import com.app.panditji.databinding.RcvRevenueItemContainerBinding

class RevenueListAdapter(
    private val context: Context?,
) :
    RecyclerView.Adapter<RevenueListAdapter.ViewHolder>() {
    var resultList: List<RecentBooking>? = null

    inner class ViewHolder(var binding: RcvRevenueItemContainerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RecentBooking, position: Int) {
            val data = resultList?.get(position)
            binding.apply {
                tvBookingId.text = data?.bookingID
                btNameId.text = data?.customerName
                tvDate.text = data?.formattedDate
                poojaName.text = data?.poojaType
                revenueAmount.text = "₹ " + data?.revenue.toString()

                when (data?.status) {
                    "completed" -> {
                        btStatusId.text = "Completed"
                        btStatusId.backgroundTintList =
                            context?.getColorStateList(R.color.green_00da45)
                    }

                    "cancelled" -> {
                        btStatusId.text = "Cancelled"
                        btStatusId.backgroundTintList =
                            context?.getColorStateList(R.color.red_e30000)
                    }

                    "accept" -> {
                        btStatusId.text = "Upcoming"
                        btStatusId.backgroundTintList =
                            context?.getColorStateList(R.color.green_00da45)
                    }

                    else -> {
                        btStatusId.text = data?.status
                        btStatusId.backgroundTintList =
                            context?.getColorStateList(R.color.green_00da45)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        if (resultList == null) {
            return 0
        } else {
            return resultList?.size!!
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RcvRevenueItemContainerBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(resultList!![position], position)
    }

    fun setData(it: List<RecentBooking>) {
        resultList = it
        notifyDataSetChanged()
    }
}