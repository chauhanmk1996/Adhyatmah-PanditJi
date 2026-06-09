package com.app.adhyatmah.domain.model.delete_account.delete_response

data class DeleteResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
)