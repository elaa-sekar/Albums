package com.task.albums.utils

import com.task.albums.data.models.local.FilterItem
import com.task.albums.data.models.local.SortItem

// Common Singleton class to hold all the static/sample/dummy data
object DataUtils {

    // To get the Static List of Filter Items
    fun getFilterItemsList(): List<FilterItem> {
        val filterMap = linkedMapOf(
            Pair(FilterType.NONE, "None"),
            Pair(FilterType.FAVORITES_ONLY, "Favourites"),
            Pair(FilterType.NON_FAVORITES_ONLY, "Non-Favourites")
        )
        ArrayList<FilterItem>().apply {
            filterMap.forEach { (key, title) -> add(FilterItem(key, title)) }
        }.also {
            return it
        }
    }

    // To get the Static List of Sort Items
    fun getSortItemsList(): List<SortItem> {
        val sortMap = linkedMapOf(
            Pair(SortType.DEFAULT, "Default"),
            Pair(SortType.TITLE_ASC, "Album Title - Asc"),
            Pair(SortType.TITLE_DESC, "Album Title - Desc"),
            Pair(SortType.USER_NAME_ASC, "User Name - Asc"),
            Pair(SortType.USER_NAME_DESC, "User Name - Des"),
        )
        ArrayList<SortItem>().apply {
            sortMap.forEach { (key, title) -> add(SortItem(key, title)) }
        }.also {
            return it
        }
    }

}