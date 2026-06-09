package com.app.panditji.core.data

import com.app.panditji.utils.extensions.getError
import kotlinx.coroutines.coroutineScope

sealed class Resource<out ResultType : Any?> {

    data class Success<out ResultType : Any?>(val data: ResultType?) : Resource<ResultType>()

    data class Error(
        var errorBody: String? = null,
        val exception: Exception? = null
    ) : Resource<Nothing>() {
        inline fun <reified E> getError() = errorBody?.getError<E>()
    }
}


suspend fun <T> Resource<T>?.throwIfError() = coroutineScope {
    when {
        this@throwIfError == null -> error("network resource is null")
        this@throwIfError is Resource.Error -> error("$errorBody")
        else -> Unit
    }
}

suspend fun <T> Resource<T>?.ifError(action: suspend (errorBody: String?) -> Unit) = coroutineScope {

    when {
        this@ifError == null -> error("network resource is null")
        this@ifError is Resource.Error -> action(errorBody)
        else -> Unit
    }
}