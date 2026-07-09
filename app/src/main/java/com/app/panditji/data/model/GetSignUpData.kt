package com.app.panditji.data.model

data class GetDesignationResponse(
    val error: Boolean,
    val code: Int,
    val status: Int,
    val message: String,
    val payload: List<SignUpData>,
)

data class GetExperienceResponse(
    val error: Boolean,
    val code: Int,
    val status: Int,
    val message: String,
    val payload: List<SignUpData>,
)

data class GetCountryResponse(
    val error: Boolean,
    val code: Int,
    val status: Int,
    val message: String,
    val payload: List<SignUpData>,
)

data class GetStateResponse(
    val error: Boolean,
    val code: Int,
    val status: Int,
    val message: String,
    val payload: List<SignUpData>,
)

data class GetCityResponse(
    val error: Boolean,
    val code: Int,
    val status: Int,
    val message: String,
    val payload: List<SignUpData>,
)

data class SignUpData(
    val id: Int,
    val name: String
)