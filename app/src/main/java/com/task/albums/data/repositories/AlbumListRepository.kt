package com.task.albums.data.repositories

import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.task.albums.data.database.dao.AlbumsDao
import com.task.albums.data.database.entities.Favourite
import com.task.albums.data.models.remote.Album
import com.task.albums.data.network.SafeApiRequest
import com.task.albums.data.network.api.ApiHandler
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlbumListRepository @Inject constructor(
    private val apiHandler: ApiHandler,
    val albumsDao: AlbumsDao
) : SafeApiRequest() {

    suspend fun requestAlbums(): List<Album> {
        return apiRequest { apiHandler.getAlbumsList() }
    }

    suspend fun saveAlbums(albumList: List<com.task.albums.data.database.entities.Album>) {
        albumsDao.insertAlbumsList(albumList)
    }

    fun getAllAlbums(rawQuery: SimpleSQLiteQuery): List<com.task.albums.data.database.entities.Album> {
        return albumsDao.getAllAlbums(rawQuery)
    }

    suspend fun getFavourite(albumId: Long): Favourite? {
        return albumsDao.getFavourite(albumId)
    }

    suspend fun updateFavourite(favourite: Favourite) {
        albumsDao.insertOrUpdateFavorite(favourite)
    }

    fun searchAlbums(searchQuery: SupportSQLiteQuery): List<com.task.albums.data.database.entities.Album> {
        return albumsDao.getAlbumsBySearch(searchQuery)
    }
}