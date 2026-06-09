package com.app.adhyatmah.domain.model.delete_account.delete_response

data class Customer(
    val addresses: List<Any>,
    val admin_graphql_api_id: String,
    val created_at: String,
    val currency: String,
    val email_marketing_consent: EmailMarketingConsent,
    val id: Long,
    val last_order_id: Any,
    val last_order_name: Any,
    val multipass_identifier: Any,
    val note: Any,
    val orders_count: Int,
    val sms_marketing_consent: Any,
    val state: String,
    val tags: String,
    val tax_exempt: Boolean,
    val tax_exemptions: List<Any>,
    val total_spent: String,
    val updated_at: String,
    val verified_email: Boolean
)