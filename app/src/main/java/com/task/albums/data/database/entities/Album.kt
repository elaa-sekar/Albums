package com.task.albums.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "album")
data class Album(
    @PrimaryKey
    val id: Long,
    val userId: Long,
    val title: String
)