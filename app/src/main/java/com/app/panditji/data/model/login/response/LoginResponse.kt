package com.app.panditji.data.model.login.response

data class LoginResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val accessToken: String,
        val customer: Customer,
        val expiresAt: String,
        val isVendor: Boolean,
        val isUser: Boolean
    ) {
        data class Customer(
            val about: String,
            val cover: Cover,
            val email: String,
            val firstName: String,
            val gender: String,
            val id: String,
            val lastName: String,
            val phone: String,
            val image: String,
            val role: String,
            val wishlist: List<Any>
        ) {
            data class Cover(
                val _id: String,
                val url: String
            )
        }
    }
}