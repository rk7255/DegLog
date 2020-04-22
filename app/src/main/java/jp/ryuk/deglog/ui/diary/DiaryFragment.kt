package jp.ryuk.deglog.ui.diary

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import jp.ryuk.deglog.R
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.databinding.FragmentDiaryBinding

/**
 *  TODO 名前変更 : Diary -> DashBoard
 */

class DiaryFragment : Fragment() {

    private lateinit var binding: FragmentDiaryBinding
    private lateinit var diaryViewModel: DiaryViewModel

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

        binding.viewModel = diaryViewModel
        binding.lifecycleOwner = this

        diaryViewModel.weightChart = binding.dbWeightChart
        diaryViewModel.lengthChart = binding.dbLengthChart

        diaryViewModel.initialized.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                setupChipGroup()
                diaryViewModel.doneInitialized()
            }
        })

        if (diaryViewModel.names.isNotEmpty()) {
            Log.d("DEBUG", diaryViewModel.selectedFilter)
            setupChipGroup(diaryViewModel.selectedFilter)
        }

        diaryViewModel.navigateToDetail.observe(viewLifecycleOwner, Observer { key ->
            if (key != null) {
                this.findNavController().navigate(
                    DiaryFragmentDirections.actionDiaryFragmentToDiaryDetailFragment(key, diaryViewModel.selectedFilter))
                diaryViewModel.doneNavigateToDetail()
            }
        })

        /**
         *  DashBoard
         */
        diaryViewModel.changeDashboard.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.dbWeightDiff.setImageResource(
                    when (diaryViewModel.weightDiff.value) {
                        "up" -> R.drawable.ic_up
                        "down" -> R.drawable.ic_down
                        else -> R.drawable.ic_flat
                    }
                )
                binding.dbLengthDiff.setImageResource(
                    when (diaryViewModel.lengthDiff.value) {
                        "up" -> R.drawable.ic_up
                        "down" -> R.drawable.ic_down
                        else -> R.drawable.ic_flat
                    }
                )
                diaryViewModel.doneChangeDashboard()
            }
        })

        return binding.root
    }

    private fun setupChipGroup(select: String = "") {
        initChipGroup(diaryViewModel.names, binding.filterChipGroup, select)
        binding.filterChipGroup.setOnCheckedChangeListener { _, _ ->
            diaryViewModel.changeFilterNames(diaryViewModel.selectedFilter, 0)
        }
        diaryViewModel.changeFilterNames(diaryViewModel.selectedFilter, 0)
    }

    @SuppressLint("InflateParams")
    private fun initChipGroup(items: List<String?>, chipGroup: ChipGroup, select: String) {
        if (items.isNotEmpty()) {
            val chipInflater = LayoutInflater.from(activity!!)
            items.forEachIndexed { index, item ->
                val chip = chipInflater.inflate(R.layout.chip_item_filter, null, false) as Chip
                chip.id = View.generateViewId()
                chip.text = item
                chip.isChecked = if (item == select) true else index == 0
                chip.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) diaryViewModel.selectedFilter = buttonView.text.toString()
                }
                chipGroup.addView(chip)
            }
            if (diaryViewModel.selectedFilter.isEmpty()) diaryViewModel.selectedFilter = items[0].toString()
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
                this.findNavController().navigate(
                    DiaryFragmentDirections
                        .actionDiaryFragmentToNewDiaryFragment(diaryViewModel.selectedFilter))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
