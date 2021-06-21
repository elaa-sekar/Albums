package com.task.albums.ui.album_list

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.task.albums.R
import com.task.albums.data.database.entities.Album
import com.task.albums.databinding.ActivityAlbumListBinding
import com.task.albums.ui.album_list.AlbumListViewModel.EventHandler.*
import com.task.albums.ui.album_list.adapter.AlbumsGridAdapter
import com.task.albums.ui.album_list.adapter.AlbumsListAdapter
import com.task.albums.ui.filter.FilterDialogFragment
import com.task.albums.utils.BindingUtils.viewBinding
import com.task.albums.utils.Coroutines
import com.task.albums.utils.EventType
import com.task.albums.utils.ViewType
import com.task.albums.utils.ViewUtils.showMessage
import com.task.albums.utils.ViewUtils.showMessageInSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@AndroidEntryPoint
class AlbumListActivity : AppCompatActivity(), AlbumListListener, FilterInputListener {

    private val binding by viewBinding(ActivityAlbumListBinding::inflate)
    private val viewModel by viewModels<AlbumListViewModel>()

    private var albumsGridAdapter: AlbumsGridAdapter? = null
    private var albumsListAdapter: AlbumsListAdapter? = null

    private var searchTextWatcher: TextWatcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewModel = viewModel
        setLayoutManager(ViewType.GRID)
        initSwipeRefreshListener()
        initOnClickListeners()
        initEventHandler()
        initObservers()
        binding.swipeRefresh.isRefreshing = true
        viewModel.getAlbums()
    }

    // One method to update swipe to refresh behaviours like updating color & setting refresh Listener
    private fun initSwipeRefreshListener() {
        binding.swipeRefresh.apply {
            setColorSchemeResources(R.color.white)
            setProgressBackgroundColorSchemeColor(
                ContextCompat.getColor(
                    this@AlbumListActivity,
                    R.color.purple_500
                )
            )
            setOnRefreshListener {
                isRefreshing = true
                viewModel.apply {
                    updateNoDataVisibility(false)
                    updateSearchBarVisibility(false)
                    setDefaultFilters()
                    getAlbums()
                }
            }
        }
    }

    // To update the layout manager when View type changed to Either Grid or List
    private fun setLayoutManager(selectedViewType: Int) {
        Timber.d("Selected ViewType $selectedViewType")
        binding.rvAlbums.layoutManager =
            if (selectedViewType == ViewType.GRID) GridLayoutManager(
                this,
                2
            ) else LinearLayoutManager(this)
    }

    /* To initialize the Livadata to observe changes in
       Album List trigger from View Model */
    private fun initObservers() {
        viewModel.apply {
            albumsLiveData.observe(this@AlbumListActivity) { albumList ->
                updateUI(albumList)
            }
        }
    }

    // Method to Update Album List UI with Other View Elements
    private fun updateUI(albumsList: List<Album>?) {
        albumsList?.let {
            updateAlbumsAdapter(albumsList.toMutableList() as ArrayList<Album>)
            viewModel.apply {
                updateOptionsVisibility(albumsList.isEmpty())
                updateNoDataVisibility(albumsList.isEmpty())
            }
        }
    }

    // Method to update the album adapter as per the selected View Type (Grid or List)
    private fun updateAlbumsAdapter(albumsList: ArrayList<Album>) {
        viewModel.selectedViewType.get()?.let {
            if (it == ViewType.GRID) switchToGridView(albumsList)
            else switchToListView(albumsList)
        }
    }

    // To initialize or update the Grid Adapter
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

    // To initialize or update the List Adapter
    private fun switchToGridView(albumsList: ArrayList<Album>) {
        binding.rvAlbums.apply {
            Timber.d("Grid View Update $adapter - ${adapter == albumsGridAdapter}")
            if (this.adapter != null && adapter == albumsGridAdapter && layoutManager is LinearLayoutManager) {
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

    // Method to handle various Events & States via ViewModel Trigger
    private fun initEventHandler() {
        lifecycleScope.launchWhenResumed {
            viewModel.eventHandler.collectLatest {
                when (it) {
                    is NotifyEvent -> {
                        showMessage(it.message)
                        try {
                            Coroutines.main {
                                if (it.eventType == EventType.ERROR) {
                                    delay(1000)
                                    showMessageInSnackBar(
                                        binding.coordinatorLayout,
                                        "Showing Offline Data Only"
                                    )
                                    delay(1000)
                                    viewModel.updateExistingAlbums()
                                }
                            }
                        } catch (e: Exception) {
                            Timber.d("Snackbar Exception $e")
                        }
                    }
                    is StartLoading -> binding.swipeRefresh.isRefreshing = true
                    is StopLoading -> binding.swipeRefresh.isRefreshing = false
                    else -> {

                    }
                }
            }
        }
    }

    // One method to set OnClick listener for each clickable views in the layout
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
                this@AlbumListActivity.viewModel.apply {
                    updateSelectedViewType()
                    selectedViewType.get()?.let { viewType -> setLayoutManager(viewType) }
                    reloadExistingAlbums()
                }
            }

            ivClose.setOnClickListener {
                removeSearchPhraseListener()
                with(this@AlbumListActivity.viewModel) {
                    updateSearchBarVisibility(false)
                    getAllAlbumsFromDB()
                }
            }
        }

    }

    // To update the same existing Album data when the view type is Changed
    private fun reloadExistingAlbums() {
        viewModel.albumsLiveData.value?.let { updateAlbumsAdapter(it.toMutableList() as ArrayList<Album>) }
    }

    // To show the filter & sorting Dialog
    private fun showFilterDialog() {
        Timber.d("Existing type ${viewModel.selectedFilterType} ${viewModel.selectedSortType}")
        FilterDialogFragment.newInstance(
            viewModel.selectedFilterType,
            viewModel.selectedSortType,
            this
        ).apply {
            show(supportFragmentManager, tag)
        }
    }

    // Method to initialize textWatcher for updating the
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

    // To remove the search text listener after closing the search EditText
    private fun removeSearchPhraseListener() {
        binding.etSearch.apply {
            text = null
            removeTextChangedListener(searchTextWatcher)
            searchTextWatcher = null
        }
    }

    // To update the
    override fun updateFavorite(albumId: Long, isFavorite: Int) {
        viewModel.updateFavourite(albumId, isFavorite)
    }

    // to update the user's input of selected Filter and Sort type upon Submit
    override fun notifyFilterSelected(selectedFilterType: Int, selectedSortType: Int) {
        viewModel.apply {
            Timber.d("Filter type $selectedFilterType Sort Type $selectedSortType")
            updateFilterTypes(selectedFilterType, selectedSortType)
            updateExistingAlbums()
        }
    }

}


