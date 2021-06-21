package com.task.albums.utils

import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

// Singleton Class to have custom Log Utility Methods
object LogUtils {

    // Method to get JSON output of Any Kotlin Object
    fun printAsJSON(data: Any): String {
        var output = ""
        try {
            output = JSONObject(Gson().toJson(data)).toString(4)
        } catch (e: Exception) {
            Timber.d("JSON Object Exception ${e.message ?: e}")
        }
        return output
    }

    // Method to get JSON output of Any Kotlin List of Objects
    fun printAsJSON(data: List<Any>): String {
        var output = ""
        try {
            output = JSONArray(Gson().toJson(data)).toString(4)
        } catch (e: Exception) {
            Timber.d("JSON Array Exception ${e.message ?: e}")
        }
        return output
    }
}