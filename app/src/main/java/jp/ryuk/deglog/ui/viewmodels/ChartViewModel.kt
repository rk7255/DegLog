package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import jp.ryuk.deglog.database.Diary
import jp.ryuk.deglog.database.DiaryRepository
import jp.ryuk.deglog.database.ProfileRepository
import jp.ryuk.deglog.ui.data.MyValueFormatter
import jp.ryuk.deglog.utilities.Utils

class ChartViewModel internal constructor(
    diaryRepository: DiaryRepository,
    profileRepository: ProfileRepository
) : ViewModel() {
    val diaries = diaryRepository.getAllDiary()
    val profiles = profileRepository.getAllProfile()
    var names = listOf<String>()

    var checked = arrayListOf<String>()

    var allLoaded = MutableLiveData<Boolean>()
    var diariesLoaded = MutableLiveData<Boolean>()
    var profilesLoaded = MutableLiveData<Boolean>()

    var hasDiaries = MutableLiveData<Boolean>()
    var filterString = MediatorLiveData<String>()

    var colorMap = mapOf<String, Int>()

    lateinit var chart: LineChart

    fun sectionLoaded() {
        if (diariesLoaded.value == true && profilesLoaded.value == true) {
            if (!diaries.value.isNullOrEmpty()) {
                hasDiaries.value = true
                names = diaries.value!!.map(Diary::name).distinct()
            } else {
                hasDiaries.value = false
            }
            allLoaded.value = true
        }
    }

    fun refreshChart(whichChart: String, whichAxis: String, names: ArrayList<String>) {
        if (names.isEmpty()) {
            hasDiaries.value = false
        } else {
            hasDiaries.value = true
            createLineChart(chart, whichChart, whichAxis, names)
        }

        val strBuilder = StringBuilder()
        strBuilder.apply {
            append("$whichChart> ")
            append("$whichAxis> ")
            names.forEachIndexed { index, name ->
                append(name)
                if (index != names.size - 1) append(", ")
            }
        }
        filterString.value = strBuilder.toString()
    }

    private fun createLineChart(
        chart: LineChart,
        whichChart: String,
        whichAxis: String,
        names: ArrayList<String>
    ) {
        val diaries = diaries.value!!.sortedBy { it.date }
        val sets = arrayListOf<ILineDataSet>()

        names.sorted().forEachIndexed { index, name ->
            var diariesByName = diaries.filter { it.name == name }

            // グラフ切替
            diariesByName = when (whichChart) {
                "体重" -> diariesByName.filter { it.weight != null }
                else -> diariesByName.filter { it.length != null }
            }

            val entries = arrayListOf<Entry>()

            diariesByName.forEachIndexed { i, diary ->
                val axis = when (whichAxis) {
                    "時間" -> diary.date.toFloat()
                    else -> i.toFloat() + 1
                }
                val data = when (whichChart) {
                    "体重" -> diary.weight!!
                    else -> diary.length!!
                }
                val entry = Entry(axis, data)
                entries.add(entry)
            }

            val profile = profiles.value!!.find { it.name == name }
            val colorKey = Utils.colorSelector(profile?.color)

            val set = LineDataSet(entries, name)
            set.apply {
                setDrawValues(false)
                lineWidth = 4f
                circleRadius = 4f
                color = colorMap[colorKey] ?: error("")
                setCircleColor(colorMap[colorKey] ?: error(""))
            }
            sets.add(set)
        }

        val data = LineData(sets)

        if (data.entryCount == 0) {
            hasDiaries.value = false
        } else {
            chart.data = data

            val legend = chart.legend
            legend.apply {
                isEnabled = true
                horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                verticalAlignment = Legend.LegendVerticalAlignment.TOP
                orientation = Legend.LegendOrientation.HORIZONTAL
                setDrawInside(false)
                yOffset = 4f
            }

            chart.apply {
                isEnabled = true
                isDoubleTapToZoomEnabled = false
                setTouchEnabled(true)
                setDrawBorders(false)
                animateX(600, Easing.EaseInOutCubic)
                animateY(400, Easing.EaseInOutCubic)
                val desc = Description()
                desc.text = ""
                description = desc
                setExtraOffsets(0f, 0f, 0f, 12f)


                xAxis.apply {
                    setDrawLabels(true)
                    setDrawGridLines(true)
                    textSize = 14f
                    position = XAxis.XAxisPosition.BOTTOM
                    setLabelCount(3, false)
                    valueFormatter = MyValueFormatter()
                }

                // 縦軸左
                axisLeft.apply {
                    enableGridDashedLine(20f, 30f, 0f)
                    textSize = 14f
                }
                // 縦軸右
                axisRight.isEnabled = false
            }

            chart.invalidate()
        }
    }


}
