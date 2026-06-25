package com.app.panditji.ui.dashboard.home

import IntroSlideData
import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.app.panditji.MainActivity
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentHomeBinding
import com.app.panditji.ui.adapter.BannerAdapter
import com.app.panditji.ui.adapter.BookingListAdapter
import com.app.panditji.ui.adapter.UpComingBookingAdapter
import com.app.panditji.utils.AppConstants
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(), UpComingBookingAdapter.OnItemClickListener {
    private lateinit var binding: FragmentHomeBinding
    private var currentPage = 0
    private val autoScrollDelay = 3000L
    private val prefs by inject<PrefsHelper>()
    private val apiVm by viewModel<apiVm>()
    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var upComingBookingAdapter: UpComingBookingAdapter
    private var progressBar: Dialog? = null

    //    private val introPages = listOf(
//        IntroSlideData("", "", R.drawable.banner_img),
//        IntroSlideData("", "", R.drawable.banner_img),
//        IntroSlideData("", "", R.drawable.banner_img),
//
//    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        Log.e("TAG userId ", "${prefs.userId}")
        Log.i("TAG ", "${prefs.fcmToken}")

        try {

        } catch (e: Exception) {
            Log.e("TAG", "${e.message}")
        }

    }

    private fun initViews() {

        binding.tvRevenueBtn.setOnClickListener {
            findNavController().navigate(R.id.revenueFragment)
        }
        binding.tvRevenueBtn1.setOnClickListener {
            findNavController().navigate(R.id.bookingFragment)
        }
        binding.tvViewAll.setOnClickListener {
            val result = Bundle().apply {
                putString("selected_tab", "upcoming")
            }
            setFragmentResult("booking_request_key", result)
            (requireActivity() as MainActivity).selectBottomNavItem(R.id.menuBooking)
//            findNavController().navigate(R.id.bookingFragment, bundle)
        }


        hitBannerApi()
        hitUpcomingBookingApi()
    }

    private fun hitUpcomingBookingApi() {
//        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.getBookings("upcoming", prefs.authToken)
            .observe(
                requireActivity()
            ) { it ->
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        val data = it.data?.payload?.bookings
//                        upComingBookingAdapter.setData(data?.bookings)

                        Log.d("TAG", "listData $data")
                        if (data?.isNotEmpty() == true) {
                            loadRcvBooking(it.data.payload.bookings)

//                            binding.rcvUpComing.visibility = View.VISIBLE
//                            binding.tvNoSlots.visibility = View.GONE

                        } else {
//                            binding.tvNoSlots.visibility = View.VISIBLE
//                            binding.rcvUpComing.visibility = View.GONE
                        }
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

    private fun hitBannerApi() {
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.getBanner()
            .observe(
                requireActivity()
            ) { it ->
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        val data = it.data?.payload
                        val adapter = BannerAdapter(data?.slides ?: listOf())
                        binding.bannerViewPager.adapter = adapter
                        binding.dotsIndicator.setViewPager2(binding.bannerViewPager)
                        startAutoScroll()
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

    private fun loadRcvBooking(list: List<GetBookingResponse.Payload.Booking>) {
        binding.rcvUpComingList.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        upComingBookingAdapter = UpComingBookingAdapter(
            requireActivity(),
            this@HomeFragment,
            list.toMutableList()
        ) { data ->
            val bundle = Bundle()
            bundle.putString("from", AppConstants.PENDING)
            bundle.putParcelable("data", data)
            findNavController().navigate(R.id.upcomingBookingDetailsFragment, bundle)

        }

        binding.rcvUpComingList.adapter = upComingBookingAdapter

    }


    private fun startAutoScroll() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val itemCount = binding.bannerViewPager.adapter?.itemCount ?: return

                if (itemCount == 0) {
                    handler.removeCallbacks(this) // stop scrolling if no items
                    return
                }

                currentPage = (binding.bannerViewPager.currentItem + 1) % itemCount
                binding.bannerViewPager.setCurrentItem(currentPage, true)
                handler.postDelayed(this, autoScrollDelay)
            }
        }

        // Start the initial auto-scroll
        handler.postDelayed(runnable, autoScrollDelay)

        // Optional: Update current page manually if user swipes
        binding.bannerViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                currentPage = position
            }
        })
    }

    override fun onItemClick(data: IntroSlideData, position: Int) {

    }


}