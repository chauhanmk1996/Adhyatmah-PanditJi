package com.app.panditji.ui.dashboard.booking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.app.panditji.R
import com.app.panditji.databinding.FragmentBookingBinding
import com.app.panditji.ui.adapter.HelpCenterAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class BookingFragment : Fragment() {
    private lateinit var binding: FragmentBookingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTabs()

    }

    private fun loadTabs() {
        val fragments: List<Fragment> =
            listOf(
                PendingBookingFragment(),
                OngoingBookingFragment(),
                UpcomingBookingsFragment(),
                PreviousBookingFragment(),
                CancelledBookingFragment(),
                )
        val adapter = HelpCenterAdapter(this, fragments)
        binding.viewPager.adapter = adapter

        parentFragmentManager.setFragmentResultListener("booking_request_key", viewLifecycleOwner) { _, bundle ->
            val selectedTab = bundle.getString("selected_tab")
            if (selectedTab == "upcoming") {
                selectedTab.let {
                    binding.viewPager.post { binding.viewPager.currentItem = 2 }
                }
            }
        }


        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.pendings)
                1 -> getString(R.string.ongoing)
                2 -> getString(R.string.upcoming)
                3 -> getString(R.string.previous)
                else -> getString(R.string.cancelled)
            }
        }.attach()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                refreshFragment(tab.position)
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                refreshFragment(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
        })
    }
    private fun refreshFragment(position: Int) {
        val tag = "f$position"   // ViewPager2 fragment tag
        val fragment = childFragmentManager.findFragmentByTag(tag)

        when (fragment) {
            is PendingBookingFragment -> fragment.refresh()
            is OngoingBookingFragment -> fragment.refresh()
            is UpcomingBookingsFragment -> fragment.refresh()
            is PreviousBookingFragment -> fragment.refresh()
            is CancelledBookingFragment -> fragment.refresh()
        }
    }

}
