package com.app.panditji.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.panditji.R
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.panditji.databinding.FragmentUpcomingBookingDetailsBinding

class UpcomingBookingDetailsFragment : Fragment() {

    private lateinit var binding: FragmentUpcomingBookingDetailsBinding
    var bookingId: String? = null
    var titles: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUpcomingBookingDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val booking = arguments?.getParcelable<GetBookingResponse.Payload.Booking>("data")
        booking?.let {
            bookingId = it.bookingID
            binding.bookingId.text = "${getString(R.string.booking_id)} ${it.bookingID}"
            binding.poojaName.text = it.poojaType
            binding.address.text = it.address.streetAddress + ", " + it.address.city + ", " + it.address.state + ", " + it.address.country + " - " + it.address.zip
            binding.poojaSamagri.text = it.pujaSamagri
            binding.dateTime.text = it.dateTime
            binding.totalFees.text = "₹ ${it.paymentAmount}"
        }


        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvTittle.text = titles

    }

}