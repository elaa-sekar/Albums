package com.task.albums.data.network

import com.task.albums.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.HttpURLConnection.*


@Suppress("UNCHECKED_CAST")
abstract class SafeApiRequest {

    suspend fun <T : Any> apiRequest(call: suspend () -> Response<T>): T {

        val response = call.invoke()

        when {
            response.isSuccessful -> {
                Timber.d("SafeApiRequest success")
                return response.body()!!
            }
            response.code() == HTTP_BAD_REQUEST -> {
                JSONObject(response.errorBody()!!.string()).let {
                    throw BadRequestException(it.getString("message"))
                }
            }
            else -> {
                Timber.d("SafeApiRequest error")
                val message = StringBuilder()
                var error: String?
                withContext(Dispatchers.IO) {
                    error = response.errorBody()?.string()
                }
                error?.let {
                    try {
                        message.append(JSONObject(it).getString("message"))
                    } catch (e: JSONException) {
                        message.append("Error body not found.")
                    }
                }
                val errorMessage =
                    "Error Response : ${NetworkUtils.getHttpStatusResponseMessage(response.code())}\n$message"
                throw NetworkResponseException(response.code(), errorMessage)
            }
        }
    }
}