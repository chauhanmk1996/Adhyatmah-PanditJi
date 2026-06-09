package com.app.panditji.core.network

import com.app.panditji.data.model.signup.response.SignupResponse

data class VerifyOtpResponse(
    val code: Int?=null,
    val error: Boolean?=null,
    val message: String?=null,
    val payload: Payload?=null,
    val status: Int?=null
) {
    data class Payload(
        val accessToken: String?=null,
        val customer: Customer?=null,
        val expiresAt: String?=null,
        val isUser: Boolean?=null,
        val isVendor: Boolean?=null,
        val role: String?=null
    ) {
        data class Customer(
            val about: String?=null,
            val cover: Cover,
            val address: String?=null,
            val city: String?=null,
            val country: String?=null,
            val email: String?=null,
            val firstName: String?=null,
            val gender: String?=null,
            val id: String?=null,
            val lastName: String?=null,
            val phone: String?=null,
            val image: String?=null,
            val role: String?=null,
            val state: String?=null,
            val zip: String?=null
        ){
            data class Cover(
                val _id: String,
                val url: String
            )
        }
    }
}