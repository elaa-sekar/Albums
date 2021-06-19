package com.task.albums.data.models.remote

import com.google.gson.annotations.SerializedName

data class Album(
    @SerializedName("id") val id: Long,
    @SerializedName("userId") val userId: Long,
    @SerializedName("title") val title: String
) {
    companion object {
        fun toAlbumEntity(album: Album): com.task.albums.data.database.entities.Album {
            album.run {
                return com.task.albums.data.database.entities.Album(
                    id,
                    userId,
                    title
                )
            }
        }
    }
}