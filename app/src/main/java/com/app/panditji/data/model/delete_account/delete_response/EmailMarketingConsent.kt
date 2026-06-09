package com.app.adhyatmah.domain.model.delete_account.delete_response

data class EmailMarketingConsent(
    val consent_updated_at: Any,
    val opt_in_level: String,
    val state: String
)