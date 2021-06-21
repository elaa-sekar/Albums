package com.task.albums.ui.album_list

interface FilterInputListener {
    fun notifyFilterSelected(selectedFilterType: Int, selectedSortType: Int)
}