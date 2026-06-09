package com.app.panditji.ui.dashboard

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.panditji.data.network.SingleLiveEvent
import com.app.panditji.data.model.get_booking.GetBookingResponse
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.panditji.data.apiRepo.RepoBooking
import com.app.panditji.data.model.update_booking_status.UpdateBookingStatusRequest
import com.app.panditji.data.network.Resources

class BookingViewModel (
    private val repoBooking: RepoBooking
) : ViewModel() {

    private val bookingsLiveData = SingleLiveEvent<Resources<GetBookingResponse>>()

    fun getBookings(): LiveData<Resources<GetBookingResponse>> = bookingsLiveData

    fun hitGetBookings(status: String, token: String) {
        bookingsLiveData.postValue(Resources.loading(null))
        viewModelScope.launch {
            try {
                val response = repoBooking.getBookingsInnerApi(token, status)
                bookingsLiveData.postValue(Resources.success(response))
            } catch (e: Exception) {
                bookingsLiveData.postValue(Resources.error(e.localizedMessage ?: "Error", null))
            }
        }
    }


    private val updateBookingLiveData = SingleLiveEvent<Resources<GetBookingResponse>>()

    fun getUpdateBooking(): LiveData<Resources<GetBookingResponse>> = updateBookingLiveData

    fun hitUpdateBooking(token: String, request: UpdateBookingStatusRequest) {
        updateBookingLiveData.postValue(Resources.loading(null))

        viewModelScope.launch {
            try {
                Log.d("UpdateBooking", "API call started with request: $request")

                val response = repoBooking.updateBookingStatusApi(token, request)

                Log.d("UpdateBooking", "API call success: $response")

                updateBookingLiveData.postValue(Resources.success(response))
            } catch (e: Exception) {
                Log.e("UpdateBooking", "API call failed", e) // ✅ prints stack trace in Logcat
                updateBookingLiveData.postValue(Resources.error(e.localizedMessage ?: "Error", null))
            }
        }
    }

}