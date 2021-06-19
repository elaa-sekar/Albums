package com.task.albums.ui.album_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.albums.data.database.entities.Album
import com.task.albums.databinding.AdapterAlbumGridBinding
import com.task.albums.ui.album_list.AlbumListListener

class AlbumsGridAdapter(
    var albumsList: List<Album>,
    var listener: AlbumListListener
) : RecyclerView.Adapter<AlbumsGridAdapter.AlbumViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            AdapterAlbumGridBinding.inflate(
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

    inner class AlbumViewHolder(private val binding: AdapterAlbumGridBinding) :
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