package com.task.albums.di

import android.content.Context
import androidx.room.Room
import com.task.albums.data.database.AlbumsDataBase
import com.task.albums.data.database.dao.AlbumsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    fun provideAlbumDataBase(@ApplicationContext context: Context): AlbumsDataBase {
        return Room.databaseBuilder(
            context,
            AlbumsDataBase::class.java,
            AlbumsDataBase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideAlbumsDao(albumsDataBase: AlbumsDataBase): AlbumsDao {
        return albumsDataBase.getAlbumsDao()
    }

}