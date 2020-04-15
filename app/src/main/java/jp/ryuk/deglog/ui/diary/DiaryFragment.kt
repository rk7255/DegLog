package jp.ryuk.deglog.ui.diary

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.DiaryAdapter
import jp.ryuk.deglog.adapters.DiaryListener
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.databinding.FragmentDiaryBinding
import jp.ryuk.deglog.navigation.NavigationIconClickListener
import kotlinx.android.synthetic.main.fragment_diary.*
import kotlinx.android.synthetic.main.fragment_diary.view.*
import kotlin.random.Random

class DiaryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDiaryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_diary, container, false)

        (activity as AppCompatActivity).setSupportActionBar(binding.appBarDiary)
        binding.appBarDiary.setNavigationOnClickListener(
            NavigationIconClickListener(activity!!, binding.diaryList, AccelerateDecelerateInterpolator())
        )
        val application = requireNotNull(this.activity).application
        val diaryDatabase = DiaryRepository.getInstance(application).diaryDao
        val viewModelFactory = DiaryViewModelFactory(diaryDatabase, application)
        val diaryViewModel = ViewModelProvider(this, viewModelFactory).get(DiaryViewModel::class.java)
        binding.diaryViewModel = diaryViewModel
        binding.lifecycleOwner = this

        /**
         * RecyclerView
         */
        val adapter = DiaryAdapter(DiaryListener {  })
        binding.recyclerViewDiary.adapter = adapter
        diaryViewModel.diaries.observe(viewLifecycleOwner, Observer {
            it?.let { adapter.submitList(it) }
        })


        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_add -> {
                this.findNavController().navigate(DiaryFragmentDirections.actionDiaryFragmentToNewDiaryFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
