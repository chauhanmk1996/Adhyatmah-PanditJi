package com.app.panditji.ui.fragment

import IntroSlideData
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.panditji.R
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.data.apiVm.apiVm
import com.app.panditji.data.sharedPrefs.PrefsHelper
import com.app.panditji.databinding.FragmentRevenueBinding
import com.app.panditji.ui.adapter.RevenueListAdapter
import com.app.panditji.utils.AppUtils
import com.app.panditji.utils.extensions.getError
import com.app.panditji.utils.extensions.toast
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue


class RevenueFragment : Fragment() {

    private lateinit var binding: FragmentRevenueBinding
    private lateinit var revenueListAdapter: RevenueListAdapter
    private val apiVm by viewModel<apiVm>()
    private val prefs by inject<PrefsHelper>()
    private var progressBar: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRevenueBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressed()
        }


    }

    private fun initView() {
//        getBookingList()
        binding.rcvRevenueList.layoutManager = LinearLayoutManager(requireActivity(),
            LinearLayoutManager.VERTICAL,false)
        revenueListAdapter = RevenueListAdapter(requireActivity())
        binding.rcvRevenueList.adapter = revenueListAdapter
        getRevenueList()
    }
    private fun getRevenueList() {
        progressBar = AppUtils.progressDialog(requireActivity())
        apiVm.getPanditRevenue( prefs.userId)
            .observe(
                requireActivity()
            ) { it ->
                println("UjjwalGupta:$it")
                when (it) {
                    is Resource.Success -> {
                        progressBar?.dismiss()
                        val data = it.data?.payload?.recentBookings
//                        upComingBookingAdapter.setData(data?.bookings)

                        Log.d("TAG","listData $data")
                        if (data?.isNotEmpty() == true) {
//                            loadRcvBooking(it.data.payload.bookings)
                            revenueListAdapter.setData(it.data.payload.recentBookings)

//                            binding.rcvUpComing.visibility = View.VISIBLE
//                            binding.tvNoSlots.visibility = View.GONE

//                            bookingList.clear()
//                            bookingList.addAll(data)
//                            upComingBookingAdapter.setData(bookingList)
                        }else{
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

}