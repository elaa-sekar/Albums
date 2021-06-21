package com.task.albums.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.task.albums.databinding.DialogFilterBinding
import com.task.albums.ui.album_list.FilterInputListener
import com.task.albums.ui.filter.adapters.FilterItemsAdapter
import com.task.albums.ui.filter.adapters.SortItemsAdapter
import com.task.albums.utils.DataUtils
import com.task.albums.utils.FilterType
import com.task.albums.utils.SortType
import timber.log.Timber

class FilterDialogFragment : DialogFragment(), FilterSelectionListener {

    lateinit var binding: DialogFilterBinding
    val viewModel by viewModels<FilterViewModel>()

    var listener: FilterInputListener? = null

    companion object {
        private const val SELECTED_FILTER_TYPE = "selected_filter_type"
        private const val SELECTED_SORT_TYPE = "selected_sort_type"

        // Static method to avoid Crashes as the Dialog fragment requires empty constructor when app is resumed
        @JvmStatic
        fun newInstance(
            selectedFilterType: Int,
            selectedSortType: Int,
            listener: FilterInputListener
        ): FilterDialogFragment {
            Timber.d("Selected Filter Type 1 $selectedFilterType")
            FilterDialogFragment().apply {
                this.listener = listener
                Bundle().apply {
                    putInt(SELECTED_FILTER_TYPE, selectedFilterType)
                    putInt(SELECTED_SORT_TYPE, selectedSortType)
                }.also {
                    arguments = it
                }
            }.also {
                return it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogFilterBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        initInputDetails()
        initLayoutManagerAndAdapters()
        initOnClickListeners()
        return binding.root
    }

    // To get & set to feed the selected inputs to Viewmodel
    private fun initInputDetails() {
        arguments?.apply {
            Timber.d("Selected Filter Type 2 ${getInt(SELECTED_FILTER_TYPE, FilterType.NONE)}")
            this@FilterDialogFragment.viewModel.updateSelectedFilterTypes(
                getInt(SELECTED_FILTER_TYPE, FilterType.NONE),
                getInt(SELECTED_SORT_TYPE, SortType.TITLE_ASC)
            )
        }
    }

    // One method to initialize onClickListener for each clickable items
    private fun initOnClickListeners() {
        binding.apply {
            btnSubmit.setOnClickListener {
                this@FilterDialogFragment.viewModel.apply {
                    listener?.notifyFilterSelected(selectedFilterType, selectedSortType)
                }
                dismiss()
            }
            btnCancel.setOnClickListener { dismiss() }
        }
    }

    override fun getTheme(): Int {
        return android.R.style.Theme_Material_Light_Dialog_NoActionBar_MinWidth
    }

    // One method to set layout manager & adapter for the both recycler views ( SortTypes & FilterTypes)
    private fun initLayoutManagerAndAdapters() {
        binding.apply {
            rvFilter.apply {
                layoutManager = FlexboxLayoutManager(
                    requireContext()
                ).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                    alignItems = AlignItems.FLEX_START
                }
                adapter =
                    FilterItemsAdapter(
                        DataUtils.getFilterItemsList(),
                        this@FilterDialogFragment,
                        this@FilterDialogFragment.viewModel.selectedFilterType
                    )
            }
            rvSort.apply {
                layoutManager = FlexboxLayoutManager(requireContext())
                adapter =
                    SortItemsAdapter(
                        DataUtils.getSortItemsList(),
                        this@FilterDialogFragment,
                        this@FilterDialogFragment.viewModel.selectedSortType
                    )
            }
        }

    }

    // To update the selected filter type in ViewModel
    override fun onFilterTypeSelected(selectedFilterType: Int) {
        Timber.d("Input selectedFilterType $selectedFilterType")
        viewModel.selectedFilterType = selectedFilterType
    }

    // To update the selected sort type in ViewModel
    override fun onSortTypeSelected(selectedSortType: Int) {
        Timber.d("Input selectedSortType $selectedSortType")
        viewModel.selectedSortType = selectedSortType
    }
}
