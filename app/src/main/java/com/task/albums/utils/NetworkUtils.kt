package com.task.albums.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {

    fun isInternetAvailable(context: Context): Boolean {
        return isWifiTurnedOn(context) || isMobileDataTurnedOn(context)
    }

    private fun isWifiTurnedOn(context: Context): Boolean {

        getConnectivityManager(context)?.let {
            it.getNetworkCapabilities(it.activeNetwork)?.run {
                return hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
            }
        }
        return false
    }

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
}