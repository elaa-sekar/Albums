package com.task.albums.di

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import com.task.albums.BuildConfig
import com.task.albums.data.network.api.ApiHandler
import com.task.albums.data.network.interceptors.HeaderInterceptor
import com.task.albums.data.network.interceptors.NetworkConnectionInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("baseUrl")
    fun provideBaseUrl() = BuildConfig.BASE_URL

    @Singleton
    @Provides
    fun provideNetworkConnectionInterceptor(@ApplicationContext context: Context): NetworkConnectionInterceptor {
        return NetworkConnectionInterceptor(context)
    }

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }.also {
            return it
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        networkConnectionInterceptor: NetworkConnectionInterceptor,
        headerInterceptor: HeaderInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(45, TimeUnit.SECONDS)
            .connectTimeout(45, TimeUnit.SECONDS)
            .addNetworkInterceptor(StethoInterceptor())
            .addInterceptor(networkConnectionInterceptor)
            .addInterceptor(headerInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesHeaderInterceptor() = HeaderInterceptor()

    @Singleton
    @Provides
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create(
            GsonBuilder().serializeNulls().create()
        )
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        @Named("baseUrl") baseUrl: String,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiHandler {
        return retrofit.create(ApiHandler::class.java)
    }

}