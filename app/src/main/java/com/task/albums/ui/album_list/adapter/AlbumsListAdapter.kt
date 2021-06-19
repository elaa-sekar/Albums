package com.task.albums.ui.album_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.albums.data.database.entities.Album
import com.task.albums.databinding.AdapterAlbumGridBinding
import com.task.albums.databinding.AdapterAlbumLinearBinding
import com.task.albums.ui.album_list.AlbumListListener

class AlbumsListAdapter(
    var albumsList: List<Album>,
    var listener: AlbumListListener
) : RecyclerView.Adapter<AlbumsListAdapter.AlbumViewHolder>() {

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

    inner class AlbumViewHolder(private val binding: AdapterAlbumLinearBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Album) {
            binding.model = model
        }

        fun initOnClickListener() {
            binding.apply {
                ivFavorite.setOnClickListener {

                }
                ivAlbum.setOnClickListener {

                }
            }
        }
    }
}