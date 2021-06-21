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

    var selectedSortType: Int = -1
    var selectedFilterType: Int = -1

    init {
        setDefaultFilters()
    }

    fun setDefaultFilters() {
        updateFilterTypes(FilterType.NONE, SortType.DEFAULT)
    }

    fun updateFilterTypes(filterType: Int, sortType: Int) {
        selectedFilterType = filterType
        selectedSortType = sortType
    }

    // Data binding parameters
    val searchText = ObservableField("")
    val searchBarVisibility = ObservableField(View.GONE)

    //    val searchIconVisibility = ObservableField(View.GONE)
    val optionsVisibility = ObservableField(View.GONE)
    val noDataVisibility = ObservableField(View.GONE)
    val selectedViewType = ObservableField(ViewType.GRID)

    // One common exception handler to handle all exception occurs inside the tasks handled by Coroutines
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        triggerEvent(EventHandler.NotifyEvent(throwable.message, EventType.ERROR))
        triggerEvent(EventHandler.StopLoading)
        Timber.e(throwable)
    }

    // A specific class to handle events via Channel
    sealed class EventHandler {
        data class NotifyEvent(val message: String?, val eventType: Int) : EventHandler()
        object StartLoading : EventHandler()
        object StopLoading : EventHandler()
    }

    // A common method to trigger events which will be received by Event Handler
    private fun triggerEvent(event: EventHandler) =
        viewModelScope.launch { _eventHandler.send(event) }

    fun getAlbums() = launchTaskInBackground {
        repository.requestAlbums().let {
            if (it.isNotEmpty()) {
                repository.saveAlbums(it.toAlbumEntitiesList())
                getAllAlbumsFromDB()
            }
        }
    }

     /*One common method to run the long running tasks
     like Network request, DB Update in Worker Thread via Coroutines*/
    private fun launchTaskInBackground(function: suspend () -> Unit) =
        viewModelScope.launch(Dispatchers.IO + exceptionHandler + job) {
            function.invoke()
            triggerEvent(EventHandler.StopLoading)
        }


    fun updateSearchBarVisibility(show: Boolean) {
        searchBarVisibility.set(if (show) View.VISIBLE else View.GONE)
    }

    fun updateOptionsVisibility(isListEmpty: Boolean) {
        optionsVisibility.set(if (isListEmpty) View.GONE else View.VISIBLE)
    }

    fun updateNoDataVisibility(show: Boolean) {
        noDataVisibility.set(if (show) View.VISIBLE else View.GONE)
    }

    fun updateSelectedViewType() {
        selectedViewType.get()?.let {
            selectedViewType.set(if (it == ViewType.GRID) ViewType.LIST else ViewType.GRID)
        }
    }

    // An utility method to convert Albums from Remote Data Source Model into Album Entities List
    private suspend fun List<com.task.albums.data.models.remote.Album>.toAlbumEntitiesList(): List<Album> {
        ArrayList<Album>().apply {
            this@toAlbumEntitiesList.forEach { album ->
                add(getAlbumEntity(album))
            }
        }.let {
            return it
        }
    }

    // An support method to convert Single Album Remote Data Source Model into Single Album Entity
    private suspend fun getAlbumEntity(album: com.task.albums.data.models.remote.Album): Album {
        album.run {
            return Album(
                id = id,
                userId = userId,
                title = title,
                userName = "Unknown",
                isFavourite = repository.albumsDao.getFavourite(id)?.isFavourite ?: 0
            )
        }
    }

    // To update the favourite in DB and updating the List upon successful completion of update Query
    fun updateFavourite(albumId: Long, favorite: Int) {
        launchTaskInBackground {
            repository.updateFavourite(Favourite(albumId, favorite))
            updateExistingAlbums()
        }
    }

    // To search albums
    fun searchAlbums(searchPhrase: String) {
        launchTaskInBackground {
            val rawQuery =
                "SELECT * FROM albums WHERE title LIKE '%${searchPhrase}%' " +
                        "${setFilterTypeForQuery(true)} ORDER BY ${setSortTypeForQuery()}"
            albumsLiveData.postValue(repository.searchAlbums(SimpleSQLiteQuery(rawQuery)))
        }
    }

    // To get all the available album data from DB
    fun getAllAlbumsFromDB() {
        launchTaskInBackground {
            val rawQuery =
                "SELECT * FROM albums ${setFilterTypeForQuery(false)} ORDER BY ${setSortTypeForQuery()}"
            albumsLiveData.postValue(repository.getAllAlbums(SimpleSQLiteQuery(rawQuery)))
        }
    }

    // To update the existing the list based on search Visibility
    fun updateExistingAlbums() {
        if (searchBarVisibility.get() == View.VISIBLE) {
            searchText.get()?.let { searchAlbums(searchPhrase = it) }
        } else getAllAlbumsFromDB()
    }

    // Common short-hand method to supply selected filter type to Raw Query to get Albums List
    private fun setFilterTypeForQuery(isForSearch: Boolean): String {
        return when (selectedFilterType) {
            FilterType.NONE -> ""
            FilterType.FAVORITES_ONLY -> "${if (isForSearch) "AND" else "WHERE"} isFavourite = 1"
            FilterType.NON_FAVORITES_ONLY -> "${if (isForSearch) "AND" else "WHERE"} isFavourite = 0"
            else -> ""
        }
    }

    // Common method to supply selected Sort type to Raw Query to get Albums List
    private fun setSortTypeForQuery(): String {
        return when (selectedSortType) {
            SortType.DEFAULT -> "id"
            SortType.TITLE_ASC -> "title ASC"
            SortType.TITLE_DESC -> "title DESC"
            SortType.USER_NAME_ASC -> "userName ASC"
            SortType.USER_NAME_DESC -> "userName DESC"
            else -> "id"
        }
    }

}
