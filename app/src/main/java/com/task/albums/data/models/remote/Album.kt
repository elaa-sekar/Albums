package com.task.albums.data.models.remote

import com.google.gson.annotations.SerializedName

data class Album(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("title") val title: String
)