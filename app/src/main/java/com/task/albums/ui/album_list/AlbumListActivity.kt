package com.task.albums.ui.album_list

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.task.albums.data.database.entities.Album
import com.task.albums.databinding.ActivityAlbumListBinding
import com.task.albums.ui.album_list.AlbumListViewModel.EventHandler.*
import com.task.albums.ui.album_list.adapter.AlbumsGridAdapter
import com.task.albums.ui.album_list.adapter.AlbumsListAdapter
import com.task.albums.utils.BindingUtils.viewBinding
import com.task.albums.utils.ViewUtils.showMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AlbumListActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityAlbumListBinding::inflate)
    private val viewModel by viewModels<AlbumListViewModel>()

    val albumsGripAdapter: AlbumsGridAdapter? = null
    val albumsListAdapter: AlbumsListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewModel = viewModel
        initLayoutManager()
        initOnClickListeners()
        initEventHandler()
        initObservers()
    }

    private fun initLayoutManager() {
        binding.rvAlbums.layoutManager = LinearLayoutManager(this)
    }

    private fun initObservers() {
        viewModel.albumsLiveData.observe(this) {
            if (!it.isNullOrEmpty()) updateAlbumsAdapter(it)
        }
    }

    private fun updateAlbumsAdapter(albumsList: List<Album>) {

    }

    private fun initEventHandler() {
        lifecycleScope.launchWhenResumed {
            viewModel.eventHandler.collectLatest {
                when (it) {
                    is NotifyEvent -> showMessage(it.message)
                    is StartLoading -> {

                    }
                    is StopLoading -> {

                    }
                }
            }
        }
    }

    private fun initOnClickListeners() {
        binding.apply {
            ivSearch.setOnClickListener {

            }
            ivFilter.setOnClickListener {

            }
            ivViewType.setOnClickListener {

            }
        }
    }
}