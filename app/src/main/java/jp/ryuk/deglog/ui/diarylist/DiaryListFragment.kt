package jp.ryuk.deglog.ui.diarylist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.paging.PagedList
import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.DetailListAdapter
import jp.ryuk.deglog.adapters.DiaryListListener
import jp.ryuk.deglog.adapters.DiaryStickerDecoration
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
        binding = FragmentDiaryListBinding.inflate(inflater, container, false)
        args = DiaryListFragmentArgs.fromBundle(requireArguments())
        binding.appBarDetailList.title = args.name + getString(R.string.title_diary_detail_at_name)

        viewModel = createViewModel(requireContext(), args.name)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        when (args.from) {
            ListKey.FROM_WEIGHT -> viewModel.checkedWeight.value = true
            ListKey.FROM_LENGTH -> viewModel.checkedLength.value = true
        }

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
            if (it != null) {
                viewModel.diariesLoaded.value = true
                viewModel.sectionLoaded()
            } else {
                viewModel.diariesLoaded.value = false
            }
        })

        viewModel.profile.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                viewModel.profileLoaded.value = true
                viewModel.sectionLoaded()
            } else {
                viewModel.profileLoaded.value = false
            }
        })

        val recyclerView = binding.detailListRecyclerView
        recyclerView.itemAnimator?.changeDuration = 0
        viewModel.diaryList.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                binding.hasData = false
            } else {
                binding.hasData = true
                if (recyclerView.itemDecorationCount > 0) recyclerView.removeItemDecorationAt(0)
                val adapter = DetailListAdapter(
                    DiaryListListener { id -> onClick(id) }
                )
                recyclerView.adapter = adapter
                adapter.submitList(it)
                val decoration = DiaryStickerDecoration(requireActivity(), it)
                recyclerView.addItemDecoration(decoration)
            }
        })

        return binding.root
    }


    private fun onClick(id: Long) {
        this.findNavController().navigate(
            DiaryListFragmentDirections
                .actionDiaryListFragmentToDiaryDetailFragment(id, args.name)
        )
    }

    private fun createViewModel(context: Context, name: String): DiaryListViewModel {
        val viewModelFactory = InjectorUtil.provideDiaryListViewModelFactory(context, name)
        return ViewModelProvider(this, viewModelFactory).get(DiaryListViewModel::class.java)
    }
}
