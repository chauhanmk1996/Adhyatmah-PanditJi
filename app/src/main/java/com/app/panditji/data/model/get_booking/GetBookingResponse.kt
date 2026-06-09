package com.app.panditji.data.model.get_booking

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class GetBookingResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val bookings: List<Booking>
    ) {
        @Parcelize
        data class Booking(
            val __v: Int,
            val _id: String,
            val address: Address,
            val bookingID: String,
            val createdAt: String,
            val customer: Customer,
            val dateTime: String,
            val duration: String,
            val `package`: String,
            val paymentAmount: Int,
            val poojaType: String,
            val pujaSamagri: String,
            val service: String,
            val status: String,
            val updatedAt: String,
//            val vendor: Vender
        ): Parcelable {
            @Parcelize
            data class Address(
                val city: String,
                val country: String,
                val state: String,
                val streetAddress: String,
                val zip: String
            ): Parcelable
        }
        @Parcelize
        data class Customer(
            val _id: String,
            val firstName: String,
            val lastName: String,
            val email: String,
            val image: String?=null,
            val phone: String
        ): Parcelable
        @Parcelize
        data class Vender(
            val _id: String,
            val firstName: String,
            val lastName: String,
            val email: String,
            val image: String,
            val phone: String
        ): Parcelable
    }
}