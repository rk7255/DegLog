package jp.ryuk.deglog.ui.diary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
import java.lang.Exception

class DiaryFragment : Fragment() {

    private lateinit var binding: FragmentDiaryBinding
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_diary, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.appBarDiary)

        diaryViewModel = createViewModel()

        binding.diaryViewModel = diaryViewModel
        binding.lifecycleOwner = this

        diaryViewModel.initialized.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                setupChipGroup(diaryViewModel.names, binding.filterChipGroup)
                diaryViewModel.changeFilterNames(selectedFilter, 0)
                diaryViewModel.doneInitialized()
            }
        })

        if (diaryViewModel.names.isNotEmpty()) {
            setupChipGroup(diaryViewModel.names, binding.filterChipGroup)
        }

        binding.filterChipGroup.setOnCheckedChangeListener { _, checkedId ->
            diaryViewModel.changeFilterNames(selectedFilter, checkedId)
        }

        /**
         * RecyclerView
         */
        val adapter = DiaryAdapter(DiaryListener { /* click listener */ })
        binding.recyclerViewDiary.adapter = adapter
        diaryViewModel.filteredDiaries.observe(viewLifecycleOwner, Observer {
            it?.let { adapter.submitList(it) }
        })
        Log.d("DEBUG", "success createView")

        return binding.root
    }

    @SuppressLint("InflateParams")
    private fun setupChipGroup(items: List<String>, chipGroup: ChipGroup) {
        val chipInflater = LayoutInflater.from(activity!!)
        items.forEachIndexed { index, item ->
            val chip = chipInflater.inflate(R.layout.chip_item_filter, null, false) as Chip
            chip.text = item
            chip.id = View.generateViewId()
            chip.isChecked = index == 0
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) selectedFilter = buttonView.text.toString()
            }
            chipGroup.addView(chip)
        }
        selectedFilter = items[0]
        Log.d("DEBUG", "success setup ChipGroup")
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
