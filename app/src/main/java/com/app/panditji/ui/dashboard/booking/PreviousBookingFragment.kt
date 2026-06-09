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
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentPreviousBookingBinding
import com.app.panditji.ui.adapter.BookingListAdapter
import com.app.panditji.ui.adapter.PreviousBookingAdapter
import com.app.panditji.utils.AppConstants
import com.app.panditji.utils.AppUtils
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
    val setList = mutableListOf<IntroSlideData>()
    private val apiVm by viewModel<apiVm>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPreviousBookingBinding.inflate(layoutInflater)
        getBookingList()
        return binding.root
    }
    fun refresh() {
        getBookingList()
    }
    private fun loadRcvBooking(list: List<GetBookingResponse.Payload.Booking>) {
//        val setList = mutableListOf<IntroSlideData>()
//             setList.clear()
//        setList.addAll(listOf(IntroSlideData("Manish ice cream wala","Noida Sector 64",R.drawable.profile_image)))
        previousBookingAdapter = BookingListAdapter(
            requireActivity(),
            AppConstants.PREVIOUS,
            list.toMutableList()
        ) {
                click, data ->
            when(click){
                0 -> {
                    val bundle = Bundle()
                    bundle.putString("from", AppConstants.PREVIOUS)
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
            }

        }
        binding.rcvPrevious.adapter = previousBookingAdapter
        // previousBookingAdapter.screenFrom = "ongoing"
    }

    private fun getBookingList() {
//        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.getBookings( "previous", prefs.token)
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

                            binding.rcvPrevious.visibility = View.VISIBLE
                            binding.tvNoSlots.visibility = View.GONE

//                            bookingList.clear()
//                            bookingList.addAll(data)
//                            upComingBookingAdapter.setData(bookingList)
                        }else{
                            binding.tvNoSlots.visibility = View.VISIBLE
                            binding.rcvPrevious.visibility = View.GONE
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
    override fun onDestroyView() {
        super.onDestroyView()
        dismissProgress() // ✅ prevents leaked dialog when swiping away during load
    }
}