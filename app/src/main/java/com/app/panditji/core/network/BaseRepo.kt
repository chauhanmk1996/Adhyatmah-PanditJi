package com.app.panditji.core.network

import androidx.lifecycle.MutableLiveData
import com.app.panditji.core.data.Resource
import com.app.panditji.core.exception.NoConnectionException
import com.app.panditji.utils.extensions.asResource
import com.app.panditji.utils.listerner.isConnected
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.net.ConnectException
import java.net.UnknownHostException


typealias NetworkCall<R> = suspend () -> Response<R>

open class BaseRepo {
    suspend inline fun <R : Any> loadData(
        noinline call: NetworkCall<R>
    ): MutableLiveData<Resource<R>> = coroutineScope {

        val result = MutableLiveData<Resource<R>>()
        println("loadData: result: $result")

        // Load from network

        val res = performNetworkCall<R>(call)
        println("loadData: res: $res")

        withContext(Dispatchers.Main) { result.value = res }

        println("loadData: return result: ${result.value}")
        return@coroutineScope result
    }

    suspend inline fun <R : Any> performNetworkCall(
        crossinline call: NetworkCall<R>
    ): Resource<R> = coroutineScope {

        println("performNetworkCall() called with: call")

        var result: Resource<R>
        try {
            if (isConnected()) {
                val response = call.invoke()
                println("performNetworkCall: ${response.body()}")
                val rslt = response.asResource()
             /*   if (rslt is Resource.Error) {
                    if (response.errorBody() != null) {
                        val jsonObj = JSONObject(response.errorBody()!!.charStream().readText())
                        val msg = jsonObj.getString("message")
                        rslt.errorBody = msg
                    }
                }*/
                result = rslt
                /* if (response.isSuccessful && body != null) {
                     result = Resource.Success(body)
                 } else {
                     result = Resource.Error(response.errorBody()?.getError<E>())
                 }*/
            } else {
                result = Resource.Error(exception = NoConnectionException())
            }
        } catch (e: UnknownHostException) {
            e.printStackTrace()
            result = Resource.Error(exception = NoConnectionException())
        } catch (e: ConnectException) {
            e.printStackTrace()
            result = Resource.Error(exception = NoConnectionException())
        } catch (e: Exception) {
            e.printStackTrace()
            result = Resource.Error(exception = e)
        }

        println("performNetworkCall() result: $result")
        return@coroutineScope result
    }
}