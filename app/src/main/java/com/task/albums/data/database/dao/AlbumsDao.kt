package com.task.albums.data.database.dao

import androidx.room.*
import com.task.albums.data.database.entities.Album
import com.task.albums.data.database.entities.Favourite

@Dao
interface AlbumsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlbum(album: Album): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTopic(album: Album)

    @Transaction
    suspend fun insertAlbumsList(albums: List<Album>) {
        for (album in albums) {
            if (insertAlbum(album) == -1L) {
                insertFavourite(Favourite(album.id, false))
            } else updateTopic(album)
        }
    }

    @Query("SELECT * FROM albums ORDER BY RANDOM()")
    fun getAllAlbums(): List<Album>

    @Query("SELECT * FROM albums WHERE id = :albumId")
    suspend fun getAlbumDetails(albumId: Int): Album

    @Query("SELECT * FROM albums WHERE title LIKE '%' || :searchQuery || '%' OR userName LIKE '%' || :searchQuery || '%' ORDER BY title")
    fun getAlbumsBySearch(searchQuery: String): List<Album>

    @Query("UPDATE albums SET isFavourite  = :isFavourite WHERE id = :albumId")
    suspend fun updateFavouriteInAlbum(albumId: Long, isFavourite: Boolean)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavourite(favourite: Favourite): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFavourite(favourite: Favourite)

    @Query("SELECT * FROM favorites WHERE albumId = :albumId ")
    suspend fun getFavourite(albumId: Long): Favourite?

    @Transaction
    suspend fun insertOrUpdateFavorite(favourite: Favourite) {
        if (insertFavourite(favourite) == -1L) else updateFavourite(favourite)
        updateFavouriteInAlbum(favourite.albumId, favourite.isFavourite)
    }

}