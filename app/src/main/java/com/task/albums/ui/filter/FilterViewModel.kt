package com.task.albums.ui.filter

import androidx.lifecycle.ViewModel

class FilterViewModel : ViewModel() {

    var selectedFilterType = -1
    var selectedSortType = -1

    fun updateSelectedFilterTypes(selectedFilterType: Int, selectedSortType: Int) {
        this.selectedSortType = selectedSortType
        this.selectedFilterType = selectedFilterType
    }
}