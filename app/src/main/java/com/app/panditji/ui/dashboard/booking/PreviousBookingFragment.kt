package com.app.panditji.ui.dashboard.booking

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentPreviousBookingBinding
import com.app.panditji.ui.adapter.BookingListAdapter
import com.app.panditji.utils.AppConstants
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PreviousBookingFragment : Fragment() {

    private lateinit var binding: FragmentPreviousBookingBinding
    private lateinit var previousBookingAdapter: BookingListAdapter
    private val prefs by inject<PrefsHelper>()
    private var progressBar: Dialog? = null
    private val apiVm by viewModel<apiVm>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentPreviousBookingBinding.inflate(layoutInflater)
        getBookingList()
        return binding.root
    }

    fun refresh() {
        getBookingList()
    }

    private fun loadRcvBooking(list: List<GetBookingResponse.Payload.Booking>) {
        previousBookingAdapter = BookingListAdapter(
            requireActivity(),
            AppConstants.PREVIOUS,
            list.toMutableList(),
            callBack = { click, data ->
                when (click) {
                    0 -> {
                        val bundle = Bundle()
                        bundle.putString("from", AppConstants.PREVIOUS)
                        bundle.putParcelable("data", data)
                        findNavController().navigate(
                            R.id.action_bookingFragment_to_upcomingBookingDetailsFragment,
                            bundle
                        )
                    }

                    1 -> {
                        // Accept button clicked
                        Log.d("TAG", "Accept button clicked for booking ID: ${data.bookingID}")
//                    changeBookingStatus("accept", data.bookingID)
                    }

                    2 -> {
                        // Reject button clicked
                        Log.d("TAG", "Reject button clicked for booking ID: ${data.bookingID}")
//                    changeBookingStatus("decline", data.bookingID)
                    }
                }
            },
            callClick = { phone ->
                openDialPad(phone)
            }
        )
        binding.rvPrevious.adapter = previousBookingAdapter
    }

    private fun getBookingList() {
        apiVm.getBookings("previous", prefs.authToken).observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    progressBar?.dismiss()
                    val data = it.data?.payload?.bookings
                    Log.d("TAG", "listData $data")
                    if (data?.isNotEmpty() == true) {
                        loadRcvBooking(it.data.payload.bookings)
                        binding.rvPrevious.visibility = View.VISIBLE
                        binding.tvNoSlots.visibility = View.GONE
                    } else {
                        binding.tvNoSlots.visibility = View.VISIBLE
                        binding.rvPrevious.visibility = View.GONE
                    }
                }

                is Resource.Error -> {
                    progressBar?.dismiss()
                    when (it.exception) {
                        is NoConnectionException -> {
                            requireActivity().toast("No Internet")
                        }

                        else -> {
                            it.errorBody?.getError()?.errorMessage?.let { errorMessage ->
                                requireActivity().toast(errorMessage)
                            }
                        }
                    }
                }

                else -> {
                }
            }
        }
    }

    private fun dismissProgress() {
        try {
            if (progressBar?.isShowing == true) {
                progressBar?.dismiss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            progressBar = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dismissProgress()
    }

    private fun openDialPad(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        startActivity(intent)
    }
}