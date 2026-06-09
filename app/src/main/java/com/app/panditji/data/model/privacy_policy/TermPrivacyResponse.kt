package com.app.adhyatmah.domain.model.privacy_policy

data class TermPrivacyResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)