package com.task.albums.utils

import com.task.albums.data.database.entities.Album

object ConversionUtil {

    fun List<com.task.albums.data.models.remote.Album>.toAlbumEntity(): List<Album> {
        ArrayList<Album>().apply {
            this@toAlbumEntity.forEach { album ->
                album.run { add(Album(id, userId, title)) }
            }
        }.let {
            return it
        }
    }
}