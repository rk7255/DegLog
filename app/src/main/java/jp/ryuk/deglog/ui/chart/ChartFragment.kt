package jp.ryuk.deglog.ui.chart

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import jp.ryuk.deglog.R
import jp.ryuk.deglog.databinding.FragmentChartBinding
import jp.ryuk.deglog.ui.dashboard.DashboardFragmentDirections
import jp.ryuk.deglog.utilities.InjectorUtil
import org.json.JSONArray

const val KEY = "shared_pref"
const val KEY_CHECKED = "checked"

class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding
    private lateinit var viewModel: ChartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        viewModel = createViewModel(requireContext())

        binding.viewModel = viewModel
        viewModel.chart = binding.chartChart

        binding.apply {
            chartFilterOpen.setOnClickListener { showFilters() }
            chartFilterBg.setOnClickListener { showFilters() }
            chartButtonSubmit.setOnClickListener { onSubmit() }

            chartChart.setNoDataText("")
        }

        viewModel.apply {
            diaries.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    diariesLoaded.value = true
                    sectionLoaded()
                }
            })

            profiles.observe(viewLifecycleOwner, Observer {
                if (!it.isNullOrEmpty()) {
                    profilesLoaded.value = true
                    sectionLoaded()
                }
            })

            allLoaded.observe(viewLifecycleOwner, Observer {
                if (it == true) {
                    createChips(requireContext(), binding.chipGroupNames, names)
                    val chips = getChips()
                    val checked = loadSharedPreferences()
                    chips.forEach { chip ->
                        chip.isChecked = checked.contains(chip.text.toString())
                    }
                    onSubmit()
                }
            })

            hasDiaries.observe(viewLifecycleOwner, Observer {
                binding.hasDiaries = it == true
            })

            filterString.observe(viewLifecycleOwner, Observer {
                binding.chartFilterText.text = it
            })

        }

        return binding.root
    }

    private fun createChips(context: Context, chipGroup: ChipGroup, names: List<String>) {
        if (names.isNotEmpty()) {
            val inflater = LayoutInflater.from(context)

            names.forEach { item ->
                @SuppressLint("InflateParams")
                val chip = inflater.inflate(R.layout.item_chip, null, false) as Chip
                chip.apply {
                    id = View.generateViewId()
                    text = item
                    setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            viewModel.checked.add(buttonView.text.toString())
                        } else {
                            viewModel.checked.remove(buttonView.text.toString())
                        }
                    }
                }
                chipGroup.addView(chip)
            }
        }
    }

    private fun getChips(): ArrayList<Chip> {
        val views = binding.chartFilterContainer
        val chips = ArrayList<Chip>()
        findChips(views, chips)
        return chips
    }

    private fun findChips(view: View, chips: ArrayList<Chip>) {
        if (Chip::class.java.isInstance(view)) {
            chips.add(view as Chip)
        }

        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                findChips(view.getChildAt(i), chips)
            }
        }
    }

    private fun saveSharedPreferences() {
        val sharedPreferences = requireContext().getSharedPreferences(KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val chips = getChips()
        val checked = arrayListOf<String>()
        chips.forEach { chip ->
            if (chip.isChecked)
                checked.add(chip.text.toString())
        }

        val jsonArray = JSONArray(checked)
        editor.putString(KEY_CHECKED, jsonArray.toString())
        editor.apply()
    }

    private fun loadSharedPreferences(): ArrayList<String> {
        val sharedPreferences = requireContext().getSharedPreferences(KEY, Context.MODE_PRIVATE)
        val jsonArray = JSONArray(sharedPreferences.getString(KEY_CHECKED, "[]"))
        val array = arrayListOf<String>()
        for (i in 0 until jsonArray.length()) {
            array.add(jsonArray.get(i).toString())
        }
        return array
    }

    private fun createViewModel(context: Context): ChartViewModel {
        val viewModelFactory = InjectorUtil.provideChartViewModelFactory(context)
        return ViewModelProvider(this, viewModelFactory).get(ChartViewModel::class.java)
    }

    private fun onSubmit() {
        saveSharedPreferences()

        val chips = getChips()
        val checked = arrayListOf<String>()
        chips.forEach {
            if (it.isChecked) checked.add(it.text.toString())
        }
        val whichChart: String
        val whichAxis: String

        when {
            checked.contains(getString(R.string.weight)) -> {
                whichChart = getString(R.string.weight)
                checked.remove(getString(R.string.weight))
            }
            checked.contains(getString(R.string.length)) -> {
                whichChart = getString(R.string.length)
                checked.remove(getString(R.string.length))
            }
            else -> {
                whichChart = getString(R.string.weight)
            }
        }
        when {
            checked.contains(getString(R.string.time)) -> {
                whichAxis = getString(R.string.time)
                checked.remove(getString(R.string.time))
            }
            checked.contains(getString(R.string.recorded)) -> {
                whichAxis = getString(R.string.recorded)
                checked.remove(getString(R.string.recorded))
            }
            else -> {
                whichAxis = getString(R.string.time)
            }
        }

        viewModel.refreshChart(whichChart, whichAxis, checked)
    }

    private fun showFilters() {
        val container = binding.chartFilterContainer
        val bg = binding.chartFilterBg
        val icon = binding.chartFilterIcon
        val rotate = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_right)

        when (container.visibility) {
            View.GONE -> {
                // OPEN
                val slideIn = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_top)
                val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)

                container.startAnimation(slideIn)
                container.visibility = View.VISIBLE
                bg.startAnimation(fadeIn)
                bg.visibility = View.VISIBLE
                icon.startAnimation(rotate)
                icon.setImageResource(R.drawable.ic_less)
            }
            else -> {
                // CLOSE
                val slideOut = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_top)
                val fadeOut = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_out)

                container.startAnimation(slideOut)
                container.visibility = View.GONE
                bg.startAnimation(fadeOut)
                bg.visibility = View.GONE
                icon.startAnimation(rotate)
                icon.setImageResource(R.drawable.ic_more)
            }
        }
    }
}
