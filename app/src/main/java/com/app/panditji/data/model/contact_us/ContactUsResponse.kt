package com.app.adhyatmah.domain.model.profile.contact_us

data class ContactUsResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)