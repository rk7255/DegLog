package jp.ryuk.deglog.ui.diary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

import jp.ryuk.deglog.R
import jp.ryuk.deglog.adapters.DiaryAdapter
import jp.ryuk.deglog.adapters.DiaryListener
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.databinding.FragmentDiaryBinding
import kotlin.math.log

class DiaryFragment : Fragment() {

    private lateinit var diaryViewModel: DiaryViewModel
    private var selectedFilter = ""

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

        diaryViewModel = createViewModel()

        binding.diaryViewModel = diaryViewModel
        binding.lifecycleOwner = this

        /**
         * RecyclerView
         */
        val adapter = DiaryAdapter(DiaryListener { /* click listener */ })
        binding.recyclerViewDiary.adapter = adapter
        diaryViewModel.diaries.observe(viewLifecycleOwner, Observer {
            setupChipGroup(diaryViewModel.names, binding.filterChipGroup)
        })

        diaryViewModel.filteredDiaries.observe(viewLifecycleOwner, Observer {
            it?.let { adapter.submitList(it) }
        })

        binding.filterChipGroup.setOnCheckedChangeListener { _, checkedId ->
            diaryViewModel.changeFilterNames(selectedFilter, checkedId)
        }


        return binding.root
    }

    @SuppressLint("InflateParams")
    private fun setupChipGroup(items: List<String>, chipGroup: ChipGroup) {
        val chipInflater = LayoutInflater.from(activity!!)
        items.forEach {
            val chip = chipInflater.inflate(R.layout.chip_item_filter, null, false) as Chip
            chip.text = it
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) selectedFilter = buttonView.text.toString()
            }
            chipGroup.addView(chip)
        }
    }

    private fun createViewModel(): DiaryViewModel {
        val application = requireNotNull(this.activity).application
        val diaryDatabase = DiaryRepository.getInstance(application).diaryDao
        val viewModelFactory = DiaryViewModelFactory(diaryDatabase, application)
        return ViewModelProvider(this, viewModelFactory).get(DiaryViewModel::class.java)
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
