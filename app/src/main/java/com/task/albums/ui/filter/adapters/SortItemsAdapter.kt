package com.task.albums.ui.filter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.albums.R
import com.task.albums.data.models.local.SortItem
import com.task.albums.databinding.AdapterSortTypesBinding
import com.task.albums.ui.filter.FilterSelectionListener
import com.task.albums.utils.ViewUtils.setBackground
import com.task.albums.utils.ViewUtils.setTextColor
import timber.log.Timber

class SortItemsAdapter(
    private val sortItemsList: List<SortItem>,
    val listener: FilterSelectionListener,
    var selectedSortType: Int
) : RecyclerView.Adapter<SortItemsAdapter.SortViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SortViewHolder {
        return SortViewHolder(
            AdapterSortTypesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SortViewHolder, position: Int) {
        with(holder) {
            bind(sortItemsList[position])
            initClickListeners()
            updateUI(binding, selectedSortType == sortItemsList[position].id)
        }
    }

    private fun updateUI(sortItemBinding: AdapterSortTypesBinding, isSelected: Boolean) {
        sortItemBinding.tvSortItem.apply {
            with(isSelected) {
                setBackground(
                    this,
                    R.drawable.shape_bg_sort_item_selected,
                    R.drawable.shape_bg_sort_item_unselected
                )
                setTextColor(this, R.color.white, R.color.night_rider)
            }
        }
    }

    override fun getItemCount(): Int {
        return sortItemsList.size
    }

    inner class SortViewHolder(val binding: AdapterSortTypesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: SortItem) {
            binding.model = model
        }

        fun initClickListeners() {
            binding.tvSortItem.setOnClickListener {
                try {
                    with(sortItemsList[absoluteAdapterPosition]){
                        selectedSortType = id
                        listener.onSortTypeSelected(id)
                    }
                    notifyDataSetChanged()
                } catch (e: Exception) {
                    Timber.d("Sort Selection Listener $e")
                }
            }
        }
    }
}