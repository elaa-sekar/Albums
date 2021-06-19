package com.task.albums.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.task.albums.data.database.entities.Album
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlbum(album: Album): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTopic(album: Album)

    @Transaction
    suspend fun insertAlbumsList(albums: List<Album>) {
        for (album in albums) {
            if (insertAlbum(album) == -1L) else updateTopic(album)
        }
    }

    @Query("SELECT * FROM albums")
    fun getAllAlbums(): LiveData<List<Album>>

    @Query("SELECT * FROM albums WHERE id = :albumId")
    suspend fun getAlbumDetails(albumId: Int): Album

}