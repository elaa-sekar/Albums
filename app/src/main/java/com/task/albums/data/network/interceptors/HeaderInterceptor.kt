package com.task.albums.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        chain.request().newBuilder().apply {
            header("Content-Type", "application/json")
            header("Accept", "application/json")
        }.also {
            return chain.proceed(it.build())
        }
    }
}