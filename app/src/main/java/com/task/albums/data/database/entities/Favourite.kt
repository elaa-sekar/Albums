package com.task.albums.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favourite(
    @PrimaryKey
    val albumId: Long,
    val isFavourite: Int
)
