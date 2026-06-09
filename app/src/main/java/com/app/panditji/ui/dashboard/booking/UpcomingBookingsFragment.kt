package com.app.panditji.ui.dashboard.booking

import IntroSlideData
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.panditji.data.model.update_booking_status.UpdateBookingStatusRequest
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentUpcomingBookingsBinding
import com.app.panditji.ui.adapter.BookingListAdapter
import com.app.panditji.ui.adapter.PreviousBookingAdapter
import com.app.panditji.utils.AppConstants
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class UpcomingBookingsFragment : Fragment() {
    private lateinit var binding: FragmentUpcomingBookingsBinding
    private lateinit var previousBookingAdapter: BookingListAdapter
    private val prefs by inject<PrefsHelper>()
    private var progressBar: Dialog? = null
    private val apiVm by viewModel<apiVm>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUpcomingBookingsBinding.inflate(layoutInflater)
        getBookingList()
        return binding.root
    }
    fun refresh() {
        getBookingList()
    }
    private fun loadRcvBooking(list: List<GetBookingResponse.Payload.Booking>) {
        previousBookingAdapter = BookingListAdapter(
            requireActivity(),
            AppConstants.UP_COMING,
            list.toMutableList()
        ) { click, data ->
            when(click){
                0 -> {
                    val bundle = Bundle()
                    bundle.putString("from", AppConstants.UP_COMING)
                    bundle.putParcelable("data", data)
                    findNavController().navigate(R.id.action_bookingFragment_to_upcomingBookingDetailsFragment, bundle)
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
                3->{
                    changeBookingStatus("cancelled", data._id)

                }
            }
        }
        binding.rcvUpComing.adapter = previousBookingAdapter
    }
    private fun changeBookingStatus(status: String, bookingID: String) {
        if (!isAdded || view == null) return  // ✅ guard against detached fragment

        showProgress()
        var request = UpdateBookingStatusRequest(
            bookingId = bookingID,
            status = status
        )
        apiVm.updateBookingStatus( prefs.token, request)
            .observe(
                requireActivity()
            ) { it ->
                println("UjjwalGupta:$it")
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        val data = it.data?.payload?.bookings
//                        upComingBookingAdapter.setData(data?.bookings)

                        Log.d("TAG", "listData $data")
//                        if (data?.isNotEmpty() == true) {
//                            loadRcvBooking(it.data.payload.bookings)
//
//                            binding.rcvUpComing.visibility = View.VISIBLE
//                            binding.tvNoSlots.visibility = View.GONE
                            getBookingList()
//                        } else {
//                            binding.tvNoSlots.visibility = View.VISIBLE
//                            binding.rcvUpComing.visibility = View.GONE
//                        }
                    }

                    is Resource.Error -> {
                        progressBar?.dismiss()
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

    private fun getBookingList() {
        if (!isAdded || view == null) return  // ✅ guard against detached fragment

        showProgress()
        apiVm.getBookings( "upcoming", prefs.token)
            .observe(
                viewLifecycleOwner
            ) { it ->
                println("UjjwalGupta:$it")
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        val data = it.data?.payload?.bookings
//                        upComingBookingAdapter.setData(data?.bookings)

                        Log.d("TAG","listData $data")
                        if (data?.isNotEmpty() == true) {
                            loadRcvBooking(it.data.payload.bookings)

                            binding.rcvUpComing.visibility = View.VISIBLE
                            binding.tvNoSlots.visibility = View.GONE

//                            bookingList.clear()
//                            bookingList.addAll(data)
//                            upComingBookingAdapter.setData(bookingList)
                        }else{
                            binding.tvNoSlots.visibility = View.VISIBLE
                            binding.rcvUpComing.visibility = View.GONE
                        }
                    }

                    is Resource.Error -> {
                        progressBar?.dismiss()
                        when (it.exception) {
                            is NoConnectionException -> {
                                requireActivity().toast("No Internet")
                            }

                            else -> {
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.errorCode}",)
                                Log.e(
                                    "TAG",
                                    "loginUser: ${it.errorBody?.getError()?.errorMessage}",
                                )
                                Log.e("TAG", "loginUser: ${it.errorBody?.getError()?.statusCode}",)
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
    private fun showProgress() {
        if (!isAdded || view == null) return  // ✅ guard
        if (progressBar == null) {
            progressBar = AppUtils.progressDialogOnlyInitialize(requireContext())
        }
        if (progressBar?.isShowing == false) {
            progressBar?.show()  // ✅ only show if not already showing
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        dismissProgress() // ✅ prevents leaked dialog when swiping away during load
    }

    /*override fun onItemClicked(data: Bookings, position: Int, type: String) {
        val bundle = Bundle()
        bundle.putParcelable("data",data)
        bundle.putString("bookingId",data?.id.toString())
        Log.d("Tag jhjds",data?.id.toString())
        findNavController().navigate(R.id.bookingDetailsFragment, bundle)

    }
*/



}