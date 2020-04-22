package jp.ryuk.deglog.ui.diarydetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import jp.ryuk.deglog.R
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.databinding.FragmentDiaryDetailBinding
import jp.ryuk.deglog.ui.diarylist.lists.WeightViewModel
import jp.ryuk.deglog.ui.diarylist.lists.WeightViewModelFactory

/**
 * A simple [Fragment] subclass.
 */
class DiaryDetailFragment : Fragment() {

    private lateinit var binding: FragmentDiaryDetailBinding
    private lateinit var diaryDetailViewModel: DiaryDetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_diary_detail, container, false)
        val arguments = DiaryDetailFragmentArgs.fromBundle(arguments!!)

        diaryDetailViewModel = createViewModel(arguments.diaryKey)
        binding.lifecycleOwner = this

        diaryDetailViewModel.diary.observe(viewLifecycleOwner, Observer {
            binding.diary = diaryDetailViewModel.diary.value
        })

        return binding.root
    }

    private fun createViewModel(diaryKey: Long): DiaryDetailViewModel {
        val application = requireNotNull(this.activity).application
        val dataSourceDiary = DiaryRepository.getInstance(application).diaryDao
        val viewModelFactory =
            DiaryDetailViewModelFactory(diaryKey, dataSourceDiary)
        return ViewModelProvider(this, viewModelFactory).get(DiaryDetailViewModel::class.java)
    }

}
