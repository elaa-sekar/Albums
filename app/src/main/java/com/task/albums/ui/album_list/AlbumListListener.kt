package com.task.albums.ui.album_list

interface AlbumListListener {
    fun updateFavorite(albumId: Long, isFavorite: Boolean)
}