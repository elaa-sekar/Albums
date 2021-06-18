package com.task.albums.data.network.api

import com.task.albums.data.models.remote.Album
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiHandler {

    // Method to the Albums List
    @GET("albums")
    suspend fun getAlbumsList(): Response<List<Album>>

    // Method to get details of an Album
    @GET("albums/{id}")
    suspend fun getAlbumDetails(@Path("id") albumId: Int): Response<Album>

}