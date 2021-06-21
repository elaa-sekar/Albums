package com.task.albums.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.task.albums.data.database.dao.AlbumsDao
import com.task.albums.data.database.entities.Album
import com.task.albums.data.database.entities.Favourite

@Database(entities = [Album::class, Favourite::class], version = 6, exportSchema = false)
abstract class AlbumsDataBase : RoomDatabase() {

    abstract fun getAlbumsDao(): AlbumsDao

    companion object {
        const val DATABASE_NAME = "albums_data_base"
    }
}