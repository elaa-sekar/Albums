package com.task.albums.ui.filter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.task.albums.R
import com.task.albums.data.models.local.FilterItem
import com.task.albums.databinding.AdapterFilterTypesBinding
import com.task.albums.ui.filter.FilterSelectionListener
import com.task.albums.utils.ViewUtils.setBackground
import com.task.albums.utils.ViewUtils.setTextColor
import timber.log.Timber

class FilterItemsAdapter(
    private val filterList: List<FilterItem>,
    val listener: FilterSelectionListener,
    var selectedFilterType: Int
) : RecyclerView.Adapter<FilterItemsAdapter.FilterViewHolder>() {

    init {
        Timber.d("FilterItemsAdapter $selectedFilterType")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        return FilterViewHolder(
            AdapterFilterTypesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.apply {
            bind(filterList[position])
            initClickListeners()
            updateUI(binding, selectedFilterType == filterList[position].id)
        }
    }

    private fun updateUI(
        brandItemsBinding: AdapterFilterTypesBinding,
        isSelected: Boolean
    ) {
        brandItemsBinding.tvFilterItem.apply {
            setBackground(
                isSelected,
                R.drawable.shape_bg_filter_item_selected,
                R.drawable.shape_bg_filter_item_unselected
            )
            setTextColor(isSelected, R.color.white, R.color.night_rider)
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    inner class FilterViewHolder(val binding: AdapterFilterTypesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: FilterItem) {
            binding.model = model
        }

        fun initClickListeners() {
            binding.tvFilterItem.setOnClickListener {
                try {
                    with(filterList[absoluteAdapterPosition]) {
                        selectedFilterType = id
                        listener.onFilterTypeSelected(id)
                    }
                    notifyDataSetChanged()
                } catch (e: Exception) {
                    Timber.d("Filter Selection Listener $e")
                }
            }
        }
    }
}