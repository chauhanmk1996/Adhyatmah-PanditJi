package com.app.panditji.data.model.get_services

data class GetAllServicesResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val services: List<Service>,
        val totalServices: Int
    ) {
        data class Service(
            val createdAt: String,
            val description: String,
            val duration: String,
            val id: String,
            val poojaType: String,
            val price: Int,
            val updatedAt: String,
            val vendor: Vendor
        ) {
            data class Vendor(
                val about: String,
                val address: Address,
                val email: String,
                val experience: Any,
                val firstName: String,
                val gotra: String,
                val id: String,
                val image: Image,
                val language: List<String>,
                val lastName: String,
                val pankti: String,
                val phone: String,
                val shakha: String,
                val sutra: String,
                val veda: String
            ) {
                data class Address(
                    val city: String,
                    val country: String,
                    val state: String,
                    val street: String,
                    val zip: String
                )

                data class Image(
                    val _id: String,
                    val url: String
                )
            }
        }
    }
}