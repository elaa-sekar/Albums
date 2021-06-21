package com.task.albums.ui.filter

interface FilterSelectionListener {
    fun onFilterTypeSelected(selectedFilterType: Int)
    fun onSortTypeSelected(selectedSortType: Int)
}