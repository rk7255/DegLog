package jp.ryuk.deglog.ui.diarylist.lists.memo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController

import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.DiaryStickerDecoration
import jp.ryuk.deglog.adapters.MemoAdapter
import jp.ryuk.deglog.adapters.MemoListener
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.databinding.FragmentMemoBinding
import jp.ryuk.deglog.databinding.MemoItemBinding
import jp.ryuk.deglog.ui.diarylist.DiaryListFragmentDirections
import jp.ryuk.deglog.ui.diarylist.ListKey

class MemoFragment(private val selectedName: String) : Fragment() {

    private lateinit var binding: FragmentMemoBinding
    private lateinit var memoViewModel: MemoViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_memo, container, false)
        memoViewModel = createViewModel(selectedName)
        binding.viewModel = memoViewModel
        binding.lifecycleOwner = this


        memoViewModel.navigateToDiaryDetail.observe(viewLifecycleOwner, Observer { key ->
            if (key != null) {
                this.findNavController().navigate(
                    DiaryListFragmentDirections.actionDiaryListFragmentToDiaryDetailFragment(ListKey.FROM_WEIGHT, key, selectedName))
                memoViewModel.doneNavigateToDiary()
            }
        })

        /**
         * RecyclerView
         */
        val recyclerView = binding.recyclerViewMemo
        val adapter = MemoAdapter(MemoListener { id ->
            memoViewModel.onClickDiary(id)
        })
        recyclerView.adapter = adapter

        memoViewModel.diaries.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                val decoration = DiaryStickerDecoration(activity!!, it)
                recyclerView.addItemDecoration(decoration)
            }
        })

        return binding.root
    }

    private fun createViewModel(selectedName: String): MemoViewModel {
        val application = requireNotNull(this.activity).application
        val dataSourceDiary = DiaryRepository.getInstance(application).diaryDao
        val viewModelFactory =
            MemoViewModelFactory(
                selectedName,
                dataSourceDiary
            )
        return ViewModelProvider(this, viewModelFactory).get(MemoViewModel::class.java)
    }

}
