package com.app.adhyatmah.domain.model.faq

import com.app.panditji.data.model.faq.Payload

data class FAQResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: List<Payload>,
    val status: Int
)