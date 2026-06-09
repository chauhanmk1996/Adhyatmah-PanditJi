package com.app.panditji.core.network
import Payload
import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class ApiResponse(
    val error: Boolean,
    val code: Int,
    val status: Int,
    val message: String,
    val dev_message: String,
    val payload: Payload? = null,
) : Parcelable


@Keep
@Parcelize
data class GetSportResponse(
    val error: Boolean,
    val code: Int,
    val status: Int,
    val message: String,
    val payload: List<Sports>? = null,
) : Parcelable

@Keep
@Parcelize
data class Sports(
    val icon: String?=null,
    val id: Int?=null,
    val name: String?=null
):Parcelable



@Keep
@Parcelize
data class ResponseModel(
    val message: MessageModel? = null
) : Parcelable

@Keep
@Parcelize
data class LoginModel(
    val email: String? = null,
    val password: String? = null,
    val deviceToken:String?=null,
    val deviceType: String?=null,
):Parcelable



@Keep
@kotlinx.parcelize.Parcelize
data class Error(
    val errorCode: Int = 0,
    val statusCode: Int = 0,
    val errorMessage: String? = null
) : Parcelable


@Keep
@Parcelize
data class MessageModel(
    val success: Boolean? = null,
    val successCode: Int? = null,
    val statusCode: Int? = null,
    val successMessage: String? = null
) : Parcelable


@Keep
@Parcelize
data class LanguageModel(
    var lang: String? = null,
    var code: String = "en",
    var isChecked: Boolean = false,
) : Parcelable








