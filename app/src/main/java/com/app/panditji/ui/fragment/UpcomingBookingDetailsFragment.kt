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
import com.app.panditji.utils.AppConstants
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.hide
import com.app.panditji.utils.extensions.show
import com.bumptech.glide.Glide

class UpcomingBookingDetailsFragment : Fragment() {

    private lateinit var binding: FragmentUpcomingBookingDetailsBinding
    private var bookingId: String? = null
    private var from: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentUpcomingBookingDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        from = arguments?.getString("from")?:""
        when(from){
            AppConstants.PENDING->{
                binding.tvTittle.text = getString(R.string.pending_booking)
            }

            AppConstants.ON_GOING->{
                binding.tvTittle.text = getString(R.string.ongoing_booking)
            }

            AppConstants.UP_COMING->{
                binding.tvTittle.text = getString(R.string.upcoming_booking)
            }

            AppConstants.PREVIOUS->{
                binding.tvTittle.text = getString(R.string.previous_booking)
            }

            AppConstants.CANCELLED->{
                binding.tvTittle.text = getString(R.string.cancelled_booking)
            }
        }

        val booking = arguments?.getParcelable<GetBookingResponse.Payload.Booking>("data")
        booking?.let {


            bookingId = it.bookingID
            val bookingIdText = "${getString(R.string.booking_id)} ${it.bookingID}"
            binding.bookingId.text = bookingIdText

            Glide.with(requireContext()).load(it.customer.image)
                .placeholder(R.drawable.profile_icon)
                .error(R.drawable.profile_icon)
                .into(binding.profileImage)

            val customerName = "${it.customer.firstName} ${it.customer.lastName}"
            binding.userName.text = customerName

            binding.poojaName.text = it.poojaType

            val address = it.address.streetAddress + ", " + it.address.city + ", " + it.address.state + ", " + it.address.country + " - " + it.address.zip
            binding.address.text = address

            if (it.pujaSamagri?.pujaKit.isNullOrEmpty() && it.pujaSamagri?.instantKit.isNullOrEmpty()) {
                binding.poojaSamagriHeading.hide()
                binding.poojaSamagri.hide()
            } else {
                val pujaKit = it.pujaSamagri.pujaKit?.joinToString(", ").orEmpty()
                val instantKit = it.pujaSamagri.instantKit?.joinToString(", ").orEmpty()

                val pujaSamagri = buildString {
                    if (pujaKit.isNotEmpty()) {
                        append("${getString(R.string.puja_kit)} - $pujaKit")
                    }

                    if (instantKit.isNotEmpty()) {
                        if (isNotEmpty()) append("\n")
                        append("${getString(R.string.instant_kit)} - $instantKit")
                    }
                }

                binding.poojaSamagriHeading.show()
                binding.poojaSamagri.show()
                binding.poojaSamagri.text = pujaSamagri
            }

            binding.dateTime.text = AppUtils.formatDate(it.dateTime)

            val amount = "₹ ${it.paymentAmount}"
            binding.totalFees.text = amount
        }

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}