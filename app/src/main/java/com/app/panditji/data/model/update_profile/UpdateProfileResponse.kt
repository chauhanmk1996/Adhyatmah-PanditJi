package com.app.panditji.data.model.update_profile

data class UpdateProfileResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val user: User
    ) {
        data class User(
            val about: String,
            val commission: Int,
            val createdAt: String,
            val email: String,
            val emergencyContact: EmergencyContact,
            val firstName: String,
            val gender: String,
            val gotra: String,
            val id: String,
            val image: String,
            val isEmailVerified: Boolean,
            val language: List<String>,
            val lastName: String,
            val phone: String,
            val profileImage: String,
            val role: String,
            val updatedAt: String
        ) {
            class EmergencyContact
        }
    }
}