package jp.ryuk.deglog.ui.diarydetail.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil

import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentLengthBinding

class LengthFragment(private val selectedName: String) : Fragment() {

    private lateinit var binding: FragmentLengthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_length, container, false)

        val str = "FROM $selectedName"
        binding.lengthFrom.text = str

        /**
         * RecyclerView
         */
//        val adapter = DiaryAdapter(DiaryListener { /* click listener */ })
//        binding.recyclerViewDiaryDetail.adapter = adapter
//        diaryDetailViewModel.diaries.observe(viewLifecycleOwner, Observer {
//            it?.let { adapter.submitList(it) }
//        })

        return binding.root
    }

}
