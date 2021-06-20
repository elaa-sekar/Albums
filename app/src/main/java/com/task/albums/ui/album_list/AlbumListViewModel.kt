package com.task.albums.ui.album_list

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.task.albums.data.database.entities.Album
import com.task.albums.data.database.entities.Favourite
import com.task.albums.data.repositories.AlbumListRepository
import com.task.albums.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
open class AlbumListViewModel @Inject constructor(private val repository: AlbumListRepository) :
    ViewModel() {

    private val _eventHandler = Channel<EventHandler>()
    val eventHandler = _eventHandler.receiveAsFlow()
    private val job = SupervisorJob()

    val albumsLiveData = MutableLiveData<List<Album>>()

    var selectedSortType = SortType.TITLE_ASC
    var selectedFilterType = FilterType.NONE


    // Data binding
    val searchText = ObservableField("")
    val searchBarVisibility = ObservableField(View.GONE)

    //    val searchIconVisibility = ObservableField(View.GONE)
    val optionsVisibility = ObservableField(View.GONE)
    val selectedViewType = ObservableField(ViewType.GRID)


    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        triggerEvent(EventHandler.NotifyEvent(throwable.message))
        triggerEvent(EventHandler.StopLoading)
        Timber.e(throwable)
    }

    sealed class EventHandler {
        data class NotifyEvent(val message: String?) : EventHandler()
        object StartLoading : EventHandler()
        object StopLoading : EventHandler()
    }

    private fun triggerEvent(event: EventHandler) =
        viewModelScope.launch { _eventHandler.send(event) }

    fun getAlbums() = launchTaskInBackground {
        repository.requestAlbums().let {
            if (it.isNotEmpty()) {
                repository.saveAlbums(it.toAlbumEntitiesList())
                albumsLiveData.postValue(repository.getAllAlbums())
                updateAllAlbumsFromDB()
            }
        }
    }

    private fun launchTaskInBackground(function: suspend () -> Unit) =
        viewModelScope.launch(Dispatchers.IO + exceptionHandler + job) {
            triggerEvent(EventHandler.StartLoading)
            function.invoke()
            triggerEvent(EventHandler.StopLoading)
        }

    fun updateSearchBarVisibility(show: Boolean) {
        searchBarVisibility.set(if (show) View.VISIBLE else View.GONE)
    }

    fun updateOptionsVisibility(isListEmpty: Boolean) {
        optionsVisibility.set(if (isListEmpty) View.GONE else View.VISIBLE)
    }

    fun updateSelectedViewType() {
        selectedViewType.get()?.let {
            selectedViewType.set(if (it == ViewType.GRID) ViewType.LIST else ViewType.GRID)
        }
    }

    private suspend fun List<com.task.albums.data.models.remote.Album>.toAlbumEntitiesList(): List<Album> {
        ArrayList<Album>().apply {
            this@toAlbumEntitiesList.forEach { album ->
                add(getAlbumEntity(album))
            }
        }.let {
            return it
        }
    }

    private suspend fun getAlbumEntity(album: com.task.albums.data.models.remote.Album): Album {
        album.run {
            return Album(
                id = id,
                userId = userId,
                title = title,
                userName = "Unknown",
                isFavourite = repository.albumsDao.getFavourite(id)?.isFavourite ?: false
            )
        }
    }

    fun updateFavourite(albumId: Long, favorite: Boolean) {
        launchTaskInBackground {
            repository.updateFavourite(Favourite(albumId, favorite))
            updateExistingAlbums()
        }
    }

    fun searchAlbums(searchPhrase: String) {
        launchTaskInBackground {
            albumsLiveData.postValue(repository.searchAlbum(searchPhrase))
        }
    }

    fun updateAllAlbumsFromDB() {
        launchTaskInBackground {
            albumsLiveData.postValue(repository.getAllAlbums())
        }
    }

    fun updateExistingAlbums() {
        if (searchBarVisibility.get() == View.VISIBLE) {
            searchText.get()?.let { searchAlbums(searchPhrase = it) }
        } else updateAllAlbumsFromDB()
    }

}