package jp.ryuk.deglog.ui.diarylist.lists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.*
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.databinding.FragmentWeightBinding
import jp.ryuk.deglog.ui.diarylist.DiaryListFragmentDirections

class WeightFragment(private val selectedName: String) : Fragment() {

    private lateinit var binding: FragmentWeightBinding
    private lateinit var weightViewModel: WeightViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_weight, container, false)
        weightViewModel = createViewModel(selectedName)
        binding.viewModel = weightViewModel
        binding.lifecycleOwner = this

        weightViewModel.navigateToDiaryDetail.observe(viewLifecycleOwner, Observer { key ->
            if (key != null) {
                this.findNavController().navigate(
                    DiaryListFragmentDirections.actionDiaryListFragmentToDiaryDetailFragment(key))
                weightViewModel.doneNavigateToDiary()
            }
        })

        /**
         * RecyclerView
         */
        val recyclerView = binding.recyclerViewWeight
        val adapter = WeightAdapter(WeightListener { id ->
            weightViewModel.onClickDiary(id)
        })
        recyclerView.adapter = adapter

        weightViewModel.diaries.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
                val decoration = DiaryStickerDecoration(activity!!, it)
                recyclerView.addItemDecoration(decoration)
            }
        })

        return binding.root
    }

    private fun createViewModel(selectedName: String): WeightViewModel {
        val application = requireNotNull(this.activity).application
        val dataSourceDiary = DiaryRepository.getInstance(application).diaryDao
        val viewModelFactory =
            WeightViewModelFactory(selectedName, dataSourceDiary)
        return ViewModelProvider(this, viewModelFactory).get(WeightViewModel::class.java)
    }

}