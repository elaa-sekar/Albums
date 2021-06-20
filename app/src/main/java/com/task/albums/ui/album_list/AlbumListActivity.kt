package com.task.albums.ui.album_list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task.albums.data.database.entities.Album
import com.task.albums.databinding.ActivityAlbumListBinding
import com.task.albums.ui.album_list.AlbumListViewModel.EventHandler.*
import com.task.albums.ui.album_list.adapter.AlbumsGridAdapter
import com.task.albums.ui.album_list.adapter.AlbumsListAdapter
import com.task.albums.utils.BindingUtils.viewBinding
import com.task.albums.utils.ViewType
import com.task.albums.utils.ViewUtils.showMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class AlbumListActivity : AppCompatActivity(), AlbumListListener {

    private val binding by viewBinding(ActivityAlbumListBinding::inflate)
    private val viewModel by viewModels<AlbumListViewModel>()

    private var albumsGridAdapter: AlbumsGridAdapter? = null
    private var albumsListAdapter: AlbumsListAdapter? = null

    var searchTextWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewModel = viewModel
        setLayoutManager(ViewType.GRID)
        initOnClickListeners()
        initEventHandler()
        initObservers()
        viewModel.getAlbums()
    }

    private fun setLayoutManager(selectedViewType: Int) {
        Timber.d("Selected ViewType $selectedViewType")
        binding.rvAlbums.layoutManager =
            if (selectedViewType == ViewType.GRID) GridLayoutManager(this, 2) else LinearLayoutManager(this)
    }

    private fun initObservers() {
        viewModel.apply {
            albumsLiveData.observe(this@AlbumListActivity) { albumList ->
                updateUI(albumList)
            }
        }
    }

    private fun updateUI(albumsList: List<Album>?) {
        albumsList?.let {
            updateAlbumsAdapter(albumsList.toMutableList() as ArrayList<Album>)
            viewModel.updateOptionsVisibility(albumsList.isEmpty())
        }
    }

    private fun updateAlbumsAdapter(albumsList: ArrayList<Album>) {
        viewModel.selectedViewType.get()?.let {
            setLayoutManager(it)
            if (it == ViewType.GRID) switchToGridView(albumsList)
            else switchToListView(albumsList)
        }
    }

    private fun switchToListView(albumsList: List<Album>) {
        binding.rvAlbums.apply {
            if (adapter != null && adapter == albumsListAdapter) {
                albumsListAdapter?.notifyUpdatedList(albumsList)
            } else {
                albumsListAdapter = AlbumsListAdapter(albumsList, this@AlbumListActivity).apply {
                    stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
                adapter = albumsListAdapter
            }
        }
    }

    private fun switchToGridView(albumsList: ArrayList<Album>) {
        binding.rvAlbums.apply {
            Timber.d("Grid View Update $adapter - ${adapter == albumsGridAdapter}")
            if (this.adapter != null && adapter == albumsGridAdapter) {
                Timber.d("Grid View Update 2")
                albumsGridAdapter?.notifyUpdatedList(albumsList)
            } else {
                Timber.d("Grid View Update 3")
                albumsGridAdapter = AlbumsGridAdapter(albumsList, this@AlbumListActivity).apply {
                    stateRestorationPolicy =
                        RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
                }
                adapter = albumsGridAdapter
            }
        }
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
                this@AlbumListActivity.viewModel.updateSearchBarVisibility(true)
                initSearchPhraseListener()
            }
            ivFilter.setOnClickListener {
                showFilterDialog()
            }
            ivViewType.setOnClickListener {
                with(this@AlbumListActivity.viewModel) {
                    updateSelectedViewType()
                    reloadExistingAlbums()
                }
            }
            ivClose.setOnClickListener {
                removeSearchPhraseListener()
                with(this@AlbumListActivity.viewModel) {
                    updateSearchBarVisibility(false)
                    updateAllAlbumsFromDB()
                }
            }
        }
    }

    private fun reloadExistingAlbums() {
        viewModel.albumsLiveData.value?.let { updateAlbumsAdapter(it.toMutableList() as ArrayList<Album>) }
    }

    private fun showFilterDialog() {

    }

    private fun initSearchPhraseListener() {

        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(input: Editable?) {
                Timber.d("After Text Changed Called $input")
                input?.toString()?.let {
                    if (it.isNotEmpty()) viewModel.searchAlbums(it)
                }
            }
        }
        binding.etSearch.addTextChangedListener(searchTextWatcher)
    }

    private fun removeSearchPhraseListener() {
        binding.etSearch.apply {
            text = null
            removeTextChangedListener(searchTextWatcher)
            searchTextWatcher = null
        }
    }

    override fun updateFavorite(albumId: Long, isFavorite: Boolean) {
        viewModel.updateFavourite(albumId, isFavorite)
    }

}