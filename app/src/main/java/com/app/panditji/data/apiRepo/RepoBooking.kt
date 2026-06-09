package com.app.panditji.data.apiRepo

import com.app.panditji.data.model.get_booking.GetBookingResponse
import com.app.panditji.data.model.update_booking_status.UpdateBookingStatusRequest
import com.app.panditji.data.network.ApiInterface
import com.app.panditji.data.sharedPrefs.PrefsHelper

class RepoBooking(private val apiInterface: ApiInterface, private val prefsHelper: PrefsHelper) {

    suspend fun updateBookingStatusApi(token:String, request: UpdateBookingStatusRequest): GetBookingResponse = apiInterface.updateBookingStatusInner(token, request)
    suspend fun getBookingsInnerApi(token:String, type: String): GetBookingResponse = apiInterface.getBookingsInner(token, type)

}
