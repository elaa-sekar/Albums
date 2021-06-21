package com.task.albums.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.HttpURLConnection.*

object NetworkUtils {

    private const val HTTP_TOO_MANY_REQUESTS = 429

    // Method to check internet availability
    fun isInternetAvailable(context: Context): Boolean {
        return isWifiTurnedOn(context) || isMobileDataTurnedOn(context)
    }

    // Method to check wifi availability
    private fun isWifiTurnedOn(context: Context): Boolean {

        getConnectivityManager(context)?.let {
            it.getNetworkCapabilities(it.activeNetwork)?.run {
                return hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
        }
        return false
    }

    // Method to check Mobile Data availability
    private fun isMobileDataTurnedOn(context: Context): Boolean {

        getConnectivityManager(context)?.let {
            it.getNetworkCapabilities(it.activeNetwork)?.run {
                return hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
            }
        }
        return false
    }

    private fun getConnectivityManager(context: Context): ConnectivityManager? {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    }


    // Common method to get message for the specific newtwork response code
    fun getHttpStatusResponseMessage(httpStatus: Int): String {
        return "$httpStatus - ${
            when (httpStatus) {
                HTTP_UNAUTHORIZED -> "Unauthorized"
                HTTP_FORBIDDEN -> "Forbidden"
                HTTP_NOT_FOUND -> "Not Found"
                HTTP_BAD_METHOD -> "Method Not Allowed"
                HTTP_NOT_ACCEPTABLE -> "Not Acceptable"
                HTTP_CLIENT_TIMEOUT -> "Request Time-Out"
                HTTP_TOO_MANY_REQUESTS -> "Too Many Requests"
                HTTP_INTERNAL_ERROR -> "Internal Server Error"
                HTTP_UNAVAILABLE -> "Service Unavailable"
                HTTP_BAD_GATEWAY -> "Bad Gateway"
                HTTP_NOT_IMPLEMENTED -> "Not Implemented"
                HTTP_GATEWAY_TIMEOUT -> "Gateway Timeout"
                else -> "Unknown API Error/Exception"
            }
        }"
    }
}