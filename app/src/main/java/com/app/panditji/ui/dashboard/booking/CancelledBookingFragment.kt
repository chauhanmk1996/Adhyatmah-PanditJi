package com.app.panditji.ui.dashboard.booking

import IntroSlideData
import android.app.Dialog
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
import com.app.panditji.data.model.update_booking_status.UpdateBookingStatusRequest
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentCancelledBookingBinding
import com.app.panditji.databinding.FragmentPendingBookingBinding
import com.app.panditji.ui.adapter.BookingListAdapter
import com.app.panditji.utils.AppConstants
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class CancelledBookingFragment : Fragment() {
    private lateinit var binding: FragmentCancelledBookingBinding
    val setList = mutableListOf<IntroSlideData>()
    private lateinit var previousBookingAdapter: BookingListAdapter
    private val prefs by inject<PrefsHelper>()
    private var progressBar: Dialog? = null
    private val apiVm by viewModel<apiVm>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCancelledBookingBinding.inflate(layoutInflater)
        getBookingList()
        return binding.root
    }
    fun refresh() {
        getBookingList()
    }

    private fun loadRcvBooking(list: List<GetBookingResponse.Payload.Booking>) {
        previousBookingAdapter = BookingListAdapter(
            requireActivity(),
            AppConstants.CANCELLED,
            list.toMutableList()
        ) { click, data ->
            when(click){
                0 -> {
                    val bundle = Bundle()
                    bundle.putString("from", AppConstants.CANCELLED)
                    bundle.putParcelable("data", data)
                    findNavController().navigate(R.id.action_bookingFragment_to_upcomingBookingDetailsFragment, bundle)
                }
                1 -> {
                    changeBookingStatus("accept", data._id)
                }
                2 -> {
                    changeBookingStatus("cancelled", data._id)
                }
            }
        }
        binding.rcvOnGoing.adapter = previousBookingAdapter
    }

    private fun getBookingList() {
//        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.getBookings("cancelled", prefs.token)
            .observe(
                viewLifecycleOwner
            ) { it ->
                println("UjjwalGupta:$it")
                when (it) {
                    is Resource.Success -> {
                        dismissProgress()
                        val data = it.data?.payload?.bookings
//                        upComingBookingAdapter.setData(data?.bookings)

                        Log.d("TAG", "listData $data")
                        if (data?.isNotEmpty() == true) {
                            loadRcvBooking(it.data.payload.bookings)

                            binding.rcvOnGoing.visibility = View.VISIBLE
                            binding.tvNoSlots.visibility = View.GONE
                        } else {
                            binding.tvNoSlots.visibility = View.VISIBLE
                            binding.rcvOnGoing.visibility = View.GONE
                        }
                    }

                    is Resource.Error -> {
                        dismissProgress()
                        when (it.exception) {
                            is NoConnectionException -> {
                                requireActivity().toast("No Internet")
                            }

                            else -> {
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.errorCode}")
                                Log.e(
                                    "TAG",
                                    "loginUser: ${it.errorBody?.getError()?.errorMessage}",
                                )
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.statusCode}")
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

    private fun changeBookingStatus(status: String, bookingID: String) {
//        progressBar = AppUtils.progressDialog(requireActivity())
        var request = UpdateBookingStatusRequest(
            bookingId = bookingID,
            status = status
        )
        apiVm.updateBookingStatus( prefs.token, request)
            .observe(
                viewLifecycleOwner
            ) { it ->
                println("UjjwalGupta:$it")
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        getBookingList()
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
        if (progressBar?.isShowing == true) {
            progressBar?.dismiss()
        }
        progressBar = null
    }
    override fun onDestroyView() {
        super.onDestroyView()
        dismissProgress() // ✅ prevents leaked dialog when swiping away during load
    }

}