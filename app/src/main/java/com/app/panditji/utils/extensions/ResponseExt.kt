package com.app.panditji.utils.extensions

import com.app.panditji.core.data.Resource
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import retrofit2.Response


inline fun <reified E> ResponseBody.getError(): E? {
    println("getError() called")
    var error: E? = null
    try {
        val errorAdapter = Moshi.Builder().build().adapter(E::class.java)
        val errorString = string()
        println("getError: errorString: $errorString")
        error = errorAdapter.fromJson(errorString ?: "")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return error
}

inline fun <reified T> String?.getError(): T? {
    println("getError() called")
    var error: T? = null
    try {
        val errorAdapter = Moshi.Builder().build().adapter(T::class.java)
        println("getError: errorString: $this")
        error = errorAdapter.fromJson(this ?: "")
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return error
}

fun  <R> Response<R>.asResource(): Resource<R> {

    val body = body()

    if (isSuccessful && body!=null) {
        // Success
        return Resource.Success(body)
    }

    // Failed
    return Resource.Error(if(this.code() >= 500) "${this.code()}: Server Error" else errorBody()?.string())
}


