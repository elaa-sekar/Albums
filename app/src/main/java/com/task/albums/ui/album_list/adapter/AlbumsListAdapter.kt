package com.task.albums.ui.album_list.adapter

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.task.albums.data.database.entities.Album
import com.task.albums.databinding.AdapterAlbumLinearBinding
import com.task.albums.ui.album_list.AlbumListListener
import com.task.albums.utils.LogicUtils
import timber.log.Timber


class AlbumsListAdapter(
    var albumsList: List<Album>,
    var listener: AlbumListListener
) : RecyclerView.Adapter<AlbumsListAdapter.AlbumViewHolder>() {


    private lateinit var mRecyclerView: RecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
        mRecyclerView.itemAnimator?.changeDuration = 0
    }

//    override fun getItemId(position: Int): Long {
//        return albumsList[position].id
//    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            AdapterAlbumLinearBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.apply {
            bind(albumsList[position])
            initOnClickListener()
        }
    }

    override fun getItemCount(): Int {
        return albumsList.size
    }

    fun notifyUpdatedList(albumsList: List<Album>) {
        val recyclerViewState: Parcelable? = mRecyclerView.layoutManager?.onSaveInstanceState()
        val diffUtilResult = DiffUtil.calculateDiff(
            LogicUtils.AlbumItemDiffCallback(
                this.albumsList,
                albumsList
            )
        )
        this.albumsList = albumsList
        diffUtilResult.dispatchUpdatesTo(this)
        mRecyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    inner class AlbumViewHolder(private val binding: AdapterAlbumLinearBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Album) {
            binding.model = model
        }

        fun initOnClickListener() {
            binding.apply {
                ivFavorite.setOnClickListener {
                    try {
                        albumsList[absoluteAdapterPosition].apply {
                            listener.updateFavorite(id, if (isFavourite == 0) 1 else 0)
                        }
                    } catch (e: Exception) {
                        Timber.d("Favorite Selection Exception $e")
                    }
                }
                ivAlbum.setOnClickListener {

                }
            }
        }
    }

//    object AlbumComparator : DiffUtil.ItemCallback<Album>() {
//        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
//            return oldItem == newItem
//        }
//    }

}