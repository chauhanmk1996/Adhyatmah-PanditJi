package com.app.panditji.data.model.get_profile

import com.app.panditji.data.model.get_services.GetAllServicesResponse

data class GetProfileResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val vendor: Vendor
    ) {
        data class Vendor(
            val about: String,
            val gotra: String?=null,
            val pravar: String?=null,
            val dateOfBirth: String?=null,
            val veda: String?=null,
            val pankti: String?=null,
            val shakha: String?=null,
            val sutra: String?=null,
            val email: String,
            val image: Image,
            val aadhar: String,
            val firstName: String,
            val id: String,
            val lastName: String,
            val phone: String,
            val gender: String,
            val role: String,
            val referral_code: String,
            val address: Address,
            val services: List<GetAllServicesResponse.Payload.Service>,
            val language: List<String>,
        )
        data class Image(
            val url: String
        )

        data class Address(
            val street: String?="",
            val city: String?="",
            val country: String?="",
            val state: String?="",
            val zip: String?=""
        )
    }
}