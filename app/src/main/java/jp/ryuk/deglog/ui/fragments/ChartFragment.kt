package jp.ryuk.deglog.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import jp.ryuk.deglog.R
import jp.ryuk.deglog.database.Diary
import jp.ryuk.deglog.databinding.FragmentChartBinding
import jp.ryuk.deglog.ui.data.ChartCreator
import jp.ryuk.deglog.ui.data.ChartData
import jp.ryuk.deglog.ui.viewmodels.ChartViewModel
import jp.ryuk.deglog.utilities.*
import org.json.JSONArray

class ChartFragment : Fragment() {

    private lateinit var binding: FragmentChartBinding
    private val viewModel: ChartViewModel by viewModels {
        InjectorUtil.provideChartViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChartBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val colorMap = mutableMapOf<String, Int>()
        var dReady = false
        var pReady = false

        with(binding) {
            chartChart.setNoDataText("")
            chartFilterOpen.setOnClickListener { showFilters(binding) }
            chartFilterBg.setOnClickListener { showFilters(binding) }

            chartButtonSubmit.setOnClickListener {
                if (dReady && pReady) createChart(colorMap)
                saveSharedPreferences(binding)
            }
        }

        with(viewModel) {
            allDiary.observe(viewLifecycleOwner) {
                createNameChips(requireContext(), binding.chipGroupNames, nameList)
                loadSharedPreferences(binding)
                dReady = true
                if (dReady && pReady) createChart(colorMap)
            }

            allProfile.observe(viewLifecycleOwner) {
                val colors = Utils.getColorMap(requireContext())
                it.forEach { profile ->
                    val c = Utils.colorSelector(profile.color)
                    colorMap[profile.name] = colors[c]
                        ?: ContextCompat.getColor(requireContext(), R.color.primaryTextColor)
                }
                pReady = true
                if (dReady && pReady) createChart(colorMap)
            }
        }

        return binding.root
    }

    private fun createChart(colorMap: Map<String, Int>) {
        val cId = binding.chipGroupChart.checkedChipId
        val whichChart = binding.chipGroupChart.findViewById<Chip>(cId).text.toString()

        val aId = binding.chipGroupAxis.checkedChipId
        val whichAxis = binding.chipGroupAxis.findViewById<Chip>(aId).text.toString()

        val nameChips = Utils.findViewsWithType(binding.chipGroupNames, Chip::class.java)
        val checked = mutableListOf<String>().apply {
            nameChips.forEach { if (it.isChecked) add(it.text.toString()) }
        }

        var diaries = viewModel.allDiary.value!!
        diaries = diaries.filter { checked.contains(it.name) }
        diaries = when (whichChart) {
            "体重" -> diaries.filter { it.weight != null }
            else -> diaries.filter { it.length != null }
        }
        val chartData = convertDiaryToChartData(diaries, whichChart)

        val filterStr = StringBuilder().apply {
            append("$whichChart> ")
            append("$whichAxis> ")
            checked.forEachIndexed { index, s ->
                if (index > 0) append(", ")
                append(s)
            }
        }
        binding.chartFilterText.text = filterStr.toString()

        if (chartData.isNotEmpty()) {
            binding.hasDiaries = true
            when (whichAxis) {
                "時間" -> ChartCreator.createLineChartByDate(
                    binding.chartChart,
                    chartData,
                    checked,
                    Deco.CHART,
                    colorMap
                )
                else -> ChartCreator.createLineChartByIndex(
                    binding.chartChart,
                    chartData,
                    checked,
                    Deco.CHART,
                    colorMap
                )
            }
        } else {
            binding.hasDiaries = false
        }
    }

    private fun convertDiaryToChartData(list: List<Diary>, which: String): List<ChartData> {
        return mutableListOf<ChartData>().apply {
            list.forEach {
                add(
                    ChartData(
                        name = it.name,
                        date = it.date,
                        data = if (which == "体重") it.weight!! else it.length!!
                    )
                )
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun createNameChips(context: Context, chipGroup: ChipGroup, names: List<String>) {
        if (names.isNotEmpty()) {
            val inflater = LayoutInflater.from(context)

            names.sorted().forEach { name ->
                val chip = inflater.inflate(R.layout.item_chip, null, false) as Chip
                chip.apply {
                    id = View.generateViewId()
                    text = name
                }
                chipGroup.addView(chip)
            }
        }
    }

    private fun saveSharedPreferences(binding: FragmentChartBinding) {
        val sharedPreferences =
            requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val cId = binding.chipGroupChart.checkedChipId
        val cText = binding.chipGroupChart.findViewById<Chip>(cId).text.toString()

        val aId = binding.chipGroupAxis.checkedChipId
        val aText = binding.chipGroupAxis.findViewById<Chip>(aId).text.toString()

        val nameChips = Utils.findViewsWithType(binding.chipGroupNames, Chip::class.java)
        val nTexts = mutableListOf<String>().apply {
            nameChips.forEach { if (it.isChecked) add(it.text.toString()) }
        }
        val nTextsToJson = JSONArray(nTexts).toString()

        editor.putString(KEY_CHECKED_CHART, cText)
        editor.putString(KEY_CHECKED_AXIS, aText)
        editor.putString(KEY_CHECKED_NAME, nTextsToJson)
        editor.apply()
    }

    private fun loadSharedPreferences(binding: FragmentChartBinding) {
        val sharedPreferences =
            requireContext().getSharedPreferences(SHARED_PREF_KEY, Context.MODE_PRIVATE)

        val c = sharedPreferences.getString(KEY_CHECKED_CHART, "体重")
        val cChips = Utils.findViewsWithType(binding.chipGroupChart, Chip::class.java)
        cChips.forEach { it.isChecked = it.text.toString() == c }

        val a = sharedPreferences.getString(KEY_CHECKED_AXIS, "時間")
        val aChips = Utils.findViewsWithType(binding.chipGroupAxis, Chip::class.java)
        aChips.forEach { it.isChecked = it.text.toString() == a }

        val jsonArray = JSONArray(sharedPreferences.getString(KEY_CHECKED_NAME, "[]"))
        val nList = mutableListOf<String>().apply {
            for (i in 0 until jsonArray.length()) {
                add(jsonArray.get(i).toString())
            }
        }
        val nChips = Utils.findViewsWithType(binding.chipGroupNames, Chip::class.java)
        nChips.forEach { it.isChecked = nList.contains(it.text.toString()) }
    }

    private fun showFilters(binding: FragmentChartBinding) {
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


