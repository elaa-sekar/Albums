package com.task.albums.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.task.albums.data.database.dao.AlbumsDao
import com.task.albums.data.database.entities.Album

@Database(entities = [Album::class], version = 1, exportSchema = false)
abstract class AlbumsDataBase : RoomDatabase() {

    abstract fun getAlbumsDao(): AlbumsDao

    companion object {
        const val DATABASE_NAME = "albums_data_base"
    }
}