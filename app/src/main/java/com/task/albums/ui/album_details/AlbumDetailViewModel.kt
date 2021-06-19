package com.task.albums.ui.album_details

import androidx.lifecycle.ViewModel
import com.task.albums.data.repositories.AlbumDetailRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailViewModel @Inject constructor(val repository: AlbumDetailRepository) : ViewModel() {



}