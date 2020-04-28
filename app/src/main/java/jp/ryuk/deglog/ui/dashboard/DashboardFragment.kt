package jp.ryuk.deglog.ui.dashboard

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
import jp.ryuk.deglog.data.DiaryRepository
import jp.ryuk.deglog.data.ProfileRepository
import jp.ryuk.deglog.databinding.FragmentDashboardBinding
import jp.ryuk.deglog.ui.diarylist.ListKey


class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.appBarDiary)

        dashboardViewModel = createViewModel()

        binding.viewModel = dashboardViewModel
        binding.weight = dashboardViewModel.weight.value
        binding.length = dashboardViewModel.length.value

        binding.lifecycleOwner = this

        dashboardViewModel.weightChart = binding.dbWeightChart
        dashboardViewModel.lengthChart = binding.dbLengthChart
        binding.dbWeightChart.setNoDataText("")
        binding.dbLengthChart.setNoDataText("")

        dashboardViewModel.initialized.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                setupChipGroup()
                dashboardViewModel.doneInitialized()
            }
        })

        if (dashboardViewModel.names.isNotEmpty()) {
            Log.d("DEBUG", dashboardViewModel.selectedFilter)
            setupChipGroup(dashboardViewModel.selectedFilter)
        }

        dashboardViewModel.navigateToDetail.observe(viewLifecycleOwner, Observer { key ->
            if (key != null) {
                this.findNavController().navigate(
                    DashboardFragmentDirections.actionDiaryFragmentToDiaryDetailFragment(key, dashboardViewModel.selectedFilter))
                dashboardViewModel.doneNavigateToDetail()
            }
        })

        /**
         *  DashBoard
         */
        dashboardViewModel.changeDashboard.observe(viewLifecycleOwner, Observer {
            if (it == true) {
                binding.weight = dashboardViewModel.weight.value
                binding.length = dashboardViewModel.length.value

                binding.dbWeightDiff.setImageResource(
                    when (dashboardViewModel.weight.value!!.diff) {
                        "up" -> R.drawable.ic_up
                        "down" -> R.drawable.ic_down
                        else -> R.drawable.ic_flat
                    }
                )
                binding.dbLengthDiff.setImageResource(
                    when (dashboardViewModel.length.value!!.diff) {
                        "up" -> R.drawable.ic_up
                        "down" -> R.drawable.ic_down
                        else -> R.drawable.ic_flat
                    }
                )
                dashboardViewModel.doneChangeDashboard()
            }
        })


        return binding.root
    }

    private fun setupChipGroup(select: String = "") {
        initChipGroup(dashboardViewModel.names, binding.filterChipGroup, select)
        binding.filterChipGroup.setOnCheckedChangeListener { _, _ ->
            dashboardViewModel.changeFilterNames(dashboardViewModel.selectedFilter, 0)
        }
        dashboardViewModel.changeFilterNames(dashboardViewModel.selectedFilter, 0)
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
                    if (isChecked) dashboardViewModel.selectedFilter = buttonView.text.toString()
                }
                chipGroup.addView(chip)
            }
            if (dashboardViewModel.selectedFilter.isEmpty()) dashboardViewModel.selectedFilter = items[0].toString()
        }
    }

    private fun createViewModel(): DashboardViewModel {
        val application = requireNotNull(this.activity).application
        val diaryDatabase = DiaryRepository.getInstance(application).diaryDao
        val profileDatabase = ProfileRepository.getInstance(application).profileDao
        val viewModelFactory = DashboardViewModelFactory(diaryDatabase, profileDatabase, application)
        return ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.toolbar_add -> {
                this.findNavController().navigate(
                    DashboardFragmentDirections
                        .actionDiaryFragmentToNewDiaryFragment(
                            ListKey.FROM_UNKNOWN, -1 ,dashboardViewModel.selectedFilter))
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
