package com.app.adhyatmah.domain.model.profile.contact_us

data class Payload(
    val address: Address,
    val domain: String,
    val email: String,
    val phone: String,
    val shopName: String
)