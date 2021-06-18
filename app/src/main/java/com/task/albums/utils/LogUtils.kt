package com.task.albums.utils

import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

object LogUtils {

    fun printAsJSON(data: Any): String {
        var output = ""
        try {
            output = JSONObject(Gson().toJson(data)).toString(4)
        } catch (e: Exception) {
            Timber.d("JSON Object Exception ${e.message ?: e}")
        }
        return output
    }

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