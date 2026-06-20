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
        savedInstanceState: Bundle?,
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
            val bookingIdText = "${getString(R.string.booking_id)} ${it.bookingID}"
            binding.bookingId.text = bookingIdText
            binding.poojaName.text = it.poojaType
            val address =
                it.address.streetAddress + ", " + it.address.city + ", " + it.address.state + ", " + it.address.country + " - " + it.address.zip
            binding.address.text = address

            val pujaSamagri = buildList {
                addAll(it.pujaSamagri?.pujaKit.orEmpty())
                addAll(it.pujaSamagri?.instantKit.orEmpty())
            }.joinToString(", ")

            binding.poojaSamagri.text = pujaSamagri

            binding.dateTime.text = it.dateTime
            val amount = "₹ ${it.paymentAmount}"
            binding.totalFees.text = amount
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvTittle.text = titles

    }

}