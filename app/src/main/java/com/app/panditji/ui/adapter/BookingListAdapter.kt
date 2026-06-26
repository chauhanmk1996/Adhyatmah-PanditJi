package com.app.panditji.ui.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.panditji.databinding.RcvItemBookingBinding
import com.app.panditji.utils.AppConstants
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.hide
import com.app.panditji.utils.extensions.show
import com.bumptech.glide.Glide

class BookingListAdapter(
    private val context: Context?,
    var type: String,
    var setList: MutableList<GetBookingResponse.Payload.Booking>,
    var callBack: (Int, GetBookingResponse.Payload.Booking) -> Unit,
    var callClick: (String) -> Unit,
) : RecyclerView.Adapter<BookingListAdapter.ViewHolder>() {

    class ViewHolder(var binding: RcvItemBookingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RcvItemBookingBinding.inflate(
            (context as android.app.Activity).layoutInflater, parent, false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return setList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = setList[position]
        holder.binding.apply {
            tvBookingId.text = data.bookingID

            Glide.with(context!!).load(data.customer.image)
                .placeholder(R.drawable.profile_icon)
                .error(R.drawable.profile_icon)
                .into(imageCardView)

            val name = data.customer.firstName + " " + data.customer.lastName
            btNameId.text = name

            val address =
                data.address.streetAddress + ", " + data.address.city + ", " + data.address.state + ", " + data.address.country + ", " + data.address.zip
            tvAddressId1.text = address

            poojaName.text = data.poojaType

            if (data.pujaSamagri == null || (data.pujaSamagri.pujaKit.isNullOrEmpty() && data.pujaSamagri.instantKit.isNullOrEmpty())) {
                tvPujaSamagri.hide()
                tvPujaKit.hide()
                tvInstantKit.hide()
            } else {
                tvPujaSamagri.show()
                if (data.pujaSamagri.pujaKit.isNullOrEmpty()) {
                    tvPujaKit.hide()
                } else {
                    tvPujaKit.show()
                    var pujaKit = ""
                    data.pujaSamagri.pujaKit.forEach { kit ->
                        pujaKit = if (pujaKit.isEmpty()) {
                            "${context.getString(R.string.puja_kit)} - $kit"
                        } else {
                            "$pujaKit, $kit"
                        }
                    }
                    tvPujaKit.text = pujaKit
                }

                if (data.pujaSamagri.instantKit.isNullOrEmpty()) {
                    tvInstantKit.hide()
                } else {
                    tvInstantKit.show()
                    var instantKit = ""
                    data.pujaSamagri.instantKit.forEach { kit ->
                        instantKit = if (instantKit.isEmpty()) {
                            "${context.getString(R.string.instant_kit)} - $kit"
                        } else {
                            "$instantKit, $kit"
                        }
                    }
                    tvInstantKit.text = instantKit
                }
            }

            tvCreateTime.text = AppUtils.formatDate(data.dateTime)
        }

        when (type) {
            AppConstants.PENDING -> {
                holder.binding.btStatusId.text = "Pending"
                holder.binding.btStatusId.backgroundTintList =
                    context?.getColorStateList(R.color.yellow_e98347e5)
                holder.binding.btnCompleteId.visibility = View.GONE
                holder.binding.btnLayoutIdId.visibility = View.VISIBLE
            }

            AppConstants.ON_GOING -> {
                holder.binding.btStatusId.backgroundTintList =
                    context?.getColorStateList(R.color.green_00da45)
                holder.binding.btStatusId.text = "Ongoing"
                holder.binding.btnCompleteId.visibility = View.VISIBLE
                holder.binding.btnLayoutIdId.visibility = View.GONE
            }

            AppConstants.UP_COMING -> {
                Log.i("TAG", "onBindViewHolder: upcoming " + "$data")
                holder.binding.btStatusId.text = "Upcoming"
                holder.binding.btStatusId.backgroundTintList =
                    context?.getColorStateList(R.color.green_00da45)
                holder.binding.btnCompleteId.visibility = View.GONE
                holder.binding.btnLayoutIdId.visibility = View.GONE
                holder.binding.btnCancel.visibility = View.VISIBLE
            }

            AppConstants.CANCELLED -> {
                holder.binding.btStatusId.text = "Cancelled"
                holder.binding.btStatusId.backgroundTintList =
                    context?.getColorStateList(R.color.red_e30000)
                holder.binding.btnCompleteId.visibility = View.GONE
                holder.binding.btnLayoutIdId.visibility = View.GONE
                holder.binding.btnCancel.visibility = View.GONE
            }

            else -> {
                holder.binding.btStatusId.backgroundTintList =
                    context?.getColorStateList(R.color.green_00da45)
                holder.binding.btStatusId.text = "Completed"
                holder.binding.btnCompleteId.visibility = View.GONE
                holder.binding.btnLayoutIdId.visibility = View.GONE
            }
        }

        holder.binding.cardUpcomingContainer.setOnClickListener {
            callBack(0, data)
        }

        holder.binding.acceptIdBtn.setOnClickListener {
            callBack(1, data)
        }

        holder.binding.declineIdBtn.setOnClickListener {
            callBack(2, data)
        }

        holder.binding.btnCancel.setOnClickListener {
            callBack(3, data)
        }

        holder.binding.btnCompleteId.setOnClickListener {
            callBack(4, data)
        }

        holder.binding.ivCall.setOnClickListener {
            callClick(data.customer.phone)
        }
    }

    fun updateData(newList: List<GetBookingResponse.Payload.Booking>) {
        setList.clear()
        setList.addAll(newList)
        notifyDataSetChanged()
    }
}