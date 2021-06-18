package com.task.albums.data.repositories

import com.task.albums.data.database.dao.AlbumsDao
import com.task.albums.data.models.remote.Album
import com.task.albums.data.network.SafeApiRequest
import com.task.albums.data.network.api.ApiHandler
import javax.inject.Inject

class AlbumDetailRepository @Inject constructor(
    private val apiHandler: ApiHandler,
    private val albumDao: AlbumsDao
) : SafeApiRequest() {

    suspend fun requestAlbumDetail(albumId: Int): Album {
        return apiRequest { apiHandler.getAlbumDetails(albumId) }
    }

    suspend fun getAlbumDetail(albumId: Int): com.task.albums.data.database.entities.Album {
        return albumDao.getAlbumDetails(albumId)
    }
}