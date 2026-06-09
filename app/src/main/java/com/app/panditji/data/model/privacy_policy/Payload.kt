package com.app.adhyatmah.domain.model.privacy_policy

data class Payload(
    val privacyPolicy: PrivacyPolicy,
    val refundPolicy: RefundPolicy,
    val shippingPolicy: ShippingPolicy,
    val termsOfService: TermsOfService
)