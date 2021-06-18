package com.task.albums.data.repositories

import com.task.albums.data.database.dao.AlbumsDao
import com.task.albums.data.models.remote.Album
import com.task.albums.data.network.SafeApiRequest
import com.task.albums.data.network.api.ApiHandler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlbumListRepository @Inject constructor(
    private val apiHandler: ApiHandler,
    private val albumsDao: AlbumsDao
) : SafeApiRequest() {

    suspend fun getAlbumsListFromServer(): List<Album> {
        return apiRequest { apiHandler.getAlbumsList() }
    }

    suspend fun getAllAlbumsFromDB(): Flow<List<com.task.albums.data.database.entities.Album>> {
        return albumsDao.getAllAlbums()
    }
}