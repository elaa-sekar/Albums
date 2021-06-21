package com.task.albums.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey
    val id: Long,
    val userId: Long,
    val title: String,
    var userName: String,
    val isFavourite: Boolean
)