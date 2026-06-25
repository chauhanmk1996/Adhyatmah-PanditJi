package com.app.panditji.core.network

import android.util.Log
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
        Log.d("PanditJi:: okhhtp", "loadData START")
        val res = performNetworkCall(call)
        Log.d("PanditJi:: okhhtp", "loadData RESPONSE -> $res")
        withContext(Dispatchers.Main) {
            result.value = res
        }
        Log.d("PanditJi:: okhhtp", "loadData END")
        result
    }

    suspend inline fun <R : Any> performNetworkCall(
        crossinline call: NetworkCall<R>
    ): Resource<R> = coroutineScope {
        Log.d("PanditJi:: okhhtp", "CALL START")
        return@coroutineScope try {
            if (!isConnected()) {
                Log.e("PanditJi:: okhhtp", "NO INTERNET")
                return@coroutineScope Resource.Error(exception = NoConnectionException())
            }

            val response = call.invoke()
            Log.d("PanditJi:: okhhtp", "URL RESPONSE CODE: ${response.code()}")
            Log.d("PanditJi:: okhhtp", "BODY: ${response.body()}")
            val result = response.asResource()
            Log.d("PanditJi:: okhhtp", "PARSED RESULT: $result")
            result
        } catch (e: UnknownHostException) {
            Log.e("PanditJi:: okhhtp", "UnknownHostException", e)
            Resource.Error(exception = NoConnectionException())
        } catch (e: ConnectException) {
            Log.e("PanditJi:: okhhtp", "ConnectException", e)
            Resource.Error(exception = NoConnectionException())
        } catch (e: Exception) {
            Log.e("PanditJi:: okhhtp", "Exception", e)
            Resource.Error(exception = e)
        }
    }
}