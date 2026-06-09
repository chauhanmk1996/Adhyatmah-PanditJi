package com.app.panditji.ui.adapter

import IntroSlideData
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.panditji.R
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.panditji.databinding.RcvUpcomingBookingContainerBinding
import com.app.panditji.utils.AppUtils
import com.bumptech.glide.Glide

class UpComingBookingAdapter(
    private val context: Context?,
    private val listener: OnItemClickListener,
    val list: List<GetBookingResponse.Payload.Booking>,
    var callBack: ( GetBookingResponse.Payload.Booking) -> Unit

) :
        RecyclerView.Adapter<UpComingBookingAdapter.ViewHolder>() {
//        var resultList: List<IntroSlideData>? = null

        inner class ViewHolder(var binding: RcvUpcomingBookingContainerBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(position: Int) {
                val data = list[position]

                Log.d("tag","Infdhjh $data")
                binding.apply {
                    binding.tvUserFullName.text = data.customer.firstName + " " + data.customer.lastName
                    binding.tvLocation.text = data.address.streetAddress +", "+data.address.city+", "+data.address.state
                    binding.tvCreateTime.text = AppUtils.formatDate(data.dateTime)
                    binding.tvBookingId.text = data.bookingID
                    binding.tvHanumanPuja.text = data.poojaType
                    Glide.with(context!!).load(data.customer.image)
                        .placeholder(R.drawable.pamdit_ji)
                        .error(R.drawable.pamdit_ji)
                        .into(ivProf)
                    binding.root.setOnClickListener{
                        callBack( data)

                    }
//                    binding.ivProf.setImageResource(data.)


                    /* val imageUrl = data.image_urls

                     if (!imageUrl.isNullOrBlank()) {
                         Glide.with(itemView.context)
                             .load(imageUrl)
                             .placeholder(R.drawable.baller_i   mage4)
                             .error(R.drawable.baller_image4)
                             .into(binding.ivGroundImage)
                     } else {
                         // Load default banner manually
                         Glide.with(itemView.context)
                             .load(R.drawable.baller_image4)
                             .into(binding.ivGroundImage)
                     }
 */

//              binding.ivViewPager.setImageResource(item.imageResId)

                    //ivActivityImage.setImageResource(data.imageResId)
//                    cardMain.setOnClickListener {
//                        listener.onItemClick(data,position)
//                    }
                }

                /*  binding.rcvSports.apply {
                      layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                      val setAdapter =  InnerSportListAdapter(context)
                      adapter = setAdapter
                      resultList?.let { setAdapter.setData(it) }
                  }*/
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RcvUpcomingBookingContainerBinding.inflate(inflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(position)
        }

//        fun setData(it: List<IntroSlideData>) {
//            resultList = it
//            notifyDataSetChanged()
//        }

        interface OnItemClickListener{
            fun onItemClick(data: IntroSlideData,position: Int)
        }

}