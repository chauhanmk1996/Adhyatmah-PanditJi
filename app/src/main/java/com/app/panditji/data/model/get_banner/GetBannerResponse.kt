package com.app.panditji.data.model.get_banner

data class GetBannerResponse(
    val code: Int,
    val error: Boolean,
    val message: String,
    val payload: Payload,
    val status: Int
) {
    data class Payload(
        val banner1: Banner1,
        val banner2: Banner2,
        val slides: List<Slide>
    ) {
        data class Banner1(
            val image: Image,
            val link: String
        ) {
            data class Image(
                val _id: String,
                val url: String
            )
        }

        data class Banner2(
            val image: Image,
            val link: String
        ) {
            data class Image(
                val _id: String,
                val url: String
            )
        }

        data class Slide(
            val _id: String,
            val image: Image,
            val link: String
        ) {
            data class Image(
                val _id: String,
                val url: String
            )
        }
    }
}