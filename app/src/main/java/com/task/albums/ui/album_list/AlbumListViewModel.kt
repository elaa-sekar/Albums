package com.task.albums.ui.album_list

import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SimpleSQLiteQuery
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
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@HiltViewModel
open class AlbumListViewModel @Inject constructor(private val repository: AlbumListRepository) :
    ViewModel() {

    private val _eventHandler = Channel<EventHandler>()
    val eventHandler = _eventHandler.receiveAsFlow()
    private val job = SupervisorJob()

    val albumsLiveData = MutableLiveData<List<Album>>()

    var selectedSortType: Int = 0
    var selectedFilterType: Int = 0

    init {
        setDefaultFilters()
    }

    fun setDefaultFilters() {
        selectedSortType = SortType.TITLE_ASC
        selectedFilterType = FilterType.NONE
    }


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
            val rawQuery =
                "SELECT * FROM albums WHERE title LIKE '%${searchPhrase}%' " +
                        "${setFilterTypeForQuery(true)}ORDER BY ${setSortTypeForQuery()}"
            albumsLiveData.postValue(repository.searchAlbums(SimpleSQLiteQuery(rawQuery)))
        }
    }


    fun updateAllAlbumsFromDB() {
        launchTaskInBackground {
            val rawQuery =
                "SELECT * FROM albums ${setFilterTypeForQuery(false)}ORDER BY ${setSortTypeForQuery()}"
            albumsLiveData.postValue(repository.getAllAlbums(SimpleSQLiteQuery(rawQuery)))
        }
    }

    fun updateExistingAlbums() {
        if (searchBarVisibility.get() == View.VISIBLE) {
            searchText.get()?.let { searchAlbums(searchPhrase = it) }
        } else updateAllAlbumsFromDB()
    }

    private fun setFilterTypeForQuery(isForSearch: Boolean): String {
        return when (selectedFilterType) {
            FilterType.NONE -> ""
            FilterType.FAVORITES_ONLY -> "${if (isForSearch) "AND" else "WHERE"} isFavorite = true"
            FilterType.NON_FAVORITES_ONLY -> "${if (isForSearch) "AND" else "WHERE"} isFavorite = false"
            else -> ""
        }
    }

    private fun setSortTypeForQuery(): String {
        return when (selectedSortType) {
            SortType.TITLE_ASC -> "title ASC"
            SortType.TITLE_DESC -> "title DESC"
            SortType.USER_NAME_ASC -> "userName ASC"
            SortType.USER_NAME_DESC -> "userName DESC"
            else -> "title ASC"
        }
    }

}
