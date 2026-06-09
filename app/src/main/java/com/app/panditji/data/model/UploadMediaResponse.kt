package com.app.panditji.data.model

data class UploadMediaResponse(
    val code: Int?=null,
    val error: Boolean?=null,
    val message: String?=null,
    val payload: Payload?=null,
    val status: Int?=null,
) {
    data class Payload(
        val customerId: String?=null,
        val metafieldId: Int?=null,
        val url: String?=null,
    )
}