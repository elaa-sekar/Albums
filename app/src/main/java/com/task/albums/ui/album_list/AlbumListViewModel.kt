package com.task.albums.ui.album_list

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.events.EventHandler
import com.task.albums.data.repositories.AlbumListRepository
import com.task.albums.ui.base.BaseViewModel
import com.task.albums.utils.ConversionUtil
import com.task.albums.utils.ConversionUtil.toAlbumEntity
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

    companion object{
        const val GRID = 1
        const val LIST = 2
    }

    private val _eventHandler = Channel<EventHandler>()
    val eventHandler = _eventHandler.receiveAsFlow()

    val albumsLiveData = repository.albumsDao.getAllAlbums()

    private val job = SupervisorJob()

    // Data Binding Variables




    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        triggerBaseEvent(EventHandler.NotifyEvent(throwable.message))
        triggerBaseEvent(EventHandler.StopLoading)
        Timber.e(throwable)
    }

    sealed class EventHandler {
        data class NotifyEvent(val message: String?) : EventHandler()
        object StartLoading : EventHandler()
        object StopLoading : EventHandler()
    }

    private fun triggerBaseEvent(event: EventHandler) =
        viewModelScope.launch { _eventHandler.send(event) }

    fun getAlbums() = apiCall {
        repository.requestAlbums().let {
            if (it.isNotEmpty()) repository.saveAlbums(it.toAlbumEntity())
        }
    }

    private fun apiCall(function: suspend () -> Unit) =
        viewModelScope.launch(Dispatchers.IO + exceptionHandler + job) {
            triggerBaseEvent(EventHandler.StartLoading)
            function.invoke()
            triggerBaseEvent(EventHandler.StopLoading)
        }

}