package com.task.albums.utils

import androidx.recyclerview.widget.DiffUtil
import com.task.albums.data.database.entities.Album

object LogicUtils {

    class AlbumItemDiffCallback(
        private var oldAlbumsList: List<Album>,
        private var newAlbumsList: List<Album>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldAlbumsList.size
        }

        override fun getNewListSize(): Int {
            return newAlbumsList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldAlbumsList[oldItemPosition].id == newAlbumsList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldAlbumsList[oldItemPosition].id == newAlbumsList[newItemPosition].id
                    && oldAlbumsList[oldItemPosition].userId == newAlbumsList[newItemPosition].userId
                    && oldAlbumsList[oldItemPosition].title == newAlbumsList[newItemPosition].title
                    && oldAlbumsList[oldItemPosition].userName == newAlbumsList[newItemPosition].userName
                    && oldAlbumsList[oldItemPosition].isFavourite == newAlbumsList[newItemPosition].isFavourite
        }
    }
}