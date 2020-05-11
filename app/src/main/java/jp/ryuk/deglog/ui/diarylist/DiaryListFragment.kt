package jp.ryuk.deglog.ui.diarylist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.*
import jp.ryuk.deglog.databinding.FragmentDiaryListBinding
import jp.ryuk.deglog.utilities.InjectorUtil
import jp.ryuk.deglog.utilities.deg


class DiaryListFragment : Fragment() {

    private lateinit var binding: FragmentDiaryListBinding
    private lateinit var viewModel: DiaryListViewModel
    private lateinit var args: DiaryListFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_diary_list, container, false
        )

        args = DiaryListFragmentArgs.fromBundle(arguments!!)
        viewModel = createViewModel(requireContext(), args.name)
        binding.appBarDetailList.title = args.name + getString(R.string.title_diary_detail_at_name)

        binding.detailListCheckWeight.isChecked = viewModel.checkedWeight.value ?: false
        binding.detailListCheckLength.isChecked = viewModel.checkedLength.value ?: false
        binding.detailListCheckMemo.isChecked = viewModel.checkedMemo.value ?: false


        binding.detailListCheckWeight.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkedWeight.value = isChecked
            viewModel.applyFilter()
        }

        binding.detailListCheckLength.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkedLength.value = isChecked
            viewModel.applyFilter()
        }

        binding.detailListCheckMemo.setOnCheckedChangeListener { _, isChecked ->
            viewModel.checkedMemo.value = isChecked
            viewModel.applyFilter()
        }

        viewModel.diaries.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                viewModel.diariesLoaded.value = true
                viewModel.sectionLoaded()
            }
        })

        viewModel.profile.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.profileLoaded.value = true
                viewModel.sectionLoaded()
            }
        })

        viewModel.filteredDiaries.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                binding.hasData = false
            } else {
                binding.hasData = true
                val recyclerView = binding.detailListRecyclerView
                if (recyclerView.itemDecorationCount > 0) recyclerView.removeItemDecorationAt(0)
                val adapter = DetailListAdapter(
                    DetailListListener { id -> onClick(id) },
                    viewModel.weightUnit.value ?: "g",
                    viewModel.lengthUnit.value ?: "mm"
                )
                recyclerView.adapter = adapter
                adapter.submitList(it)
                val decoration = DiaryStickerDecoration(activity!!, it)
                recyclerView.addItemDecoration(decoration)
            }
        })

        return binding.root
    }

    private fun onClick(id: Long) {
        this.findNavController().navigate(
            DiaryListFragmentDirections
                .actionDiaryListFragmentToDiaryDetailFragment(id, args.name))
    }

    private fun createViewModel(context: Context, name: String): DiaryListViewModel {
        val viewModelFactory = InjectorUtil.provideDiaryListViewModelFactory(context, name)
        return ViewModelProvider(this, viewModelFactory).get(DiaryListViewModel::class.java)
    }
}
