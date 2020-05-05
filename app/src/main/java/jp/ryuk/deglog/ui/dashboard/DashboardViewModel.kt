package jp.ryuk.deglog.ui.dashboard

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.convertLongToDateString
import jp.ryuk.deglog.utilities.convertUnit
import jp.ryuk.deglog.utilities.deg
import java.util.*
import kotlin.math.absoluteValue

class DashboardViewModel(
    diaryDatabase: DiaryDao,
    profileDatabase: ProfileDao
) : ViewModel() {

    val diaries: LiveData<List<Diary>> = diaryDatabase.getAllDiaries()
    val names: LiveData<List<String>> = diaryDatabase.getNamesInDiaryDB()
    val profiles: LiveData<List<Profile>> = profileDatabase.getProfilesLive()
    var selected = MediatorLiveData<String?>()

    var allLoaded = MutableLiveData<Boolean>()
    var diariesLoaded = MutableLiveData<Boolean>()
    var profilesLoaded = MutableLiveData<Boolean>()
    var namesLoaded = MutableLiveData<Boolean>()

    fun sectionLoaded() {
        if (diariesLoaded.value == true
            && profilesLoaded.value == true
            && namesLoaded.value == true) {
            allLoaded.value = true
            changeDashboard()
        }
    }

    // onClick
    private var _call = MutableLiveData<Int?>()
    val call: LiveData<Int?> get() = _call
    fun onCall(key: Int) { _call.value = key }
    fun doneCall() { _call.value = null }

    // Display Data
    var weight = MediatorLiveData<Dashboard>()
    var length = MediatorLiveData<Dashboard>()
    var age = MediatorLiveData<String>()

    lateinit var chartWeight: LineChart
    lateinit var chartLength: LineChart

    fun changeDashboard() {
        val filtered = diaries.value!!.filter { it.name == selected.value!! }
        val profile = profiles.value!!.find { it.name == selected.value }

        var weights = filtered.filter { it.weight != null }
        if (weights.size >= 7) weights = weights.subList(0, 7)
        var lengths = filtered.filter { it.length != null }
        if (lengths.size >= 7) lengths = lengths.subList(0, 7)

        val newWeight = Dashboard()
        if (weights.isEmpty()) {
            newWeight.unit = "g"
        } else {
            val list = weights.mapNotNull(Diary::weight)
            newWeight.unit = profile!!.weightUnit
            newWeight.latest = latest(list, newWeight.unit)
            newWeight.prev = diff(list, newWeight.unit, "prev")
            newWeight.prevPlus = sign(list, newWeight.unit, "prev")
            newWeight.recent = diff(list, newWeight.unit, "recent")
            newWeight.recentPlus = sign(list, newWeight.unit, "recent")
            newWeight.date = date(weights[0].date)
        }

        val newLength = Dashboard()
        if (lengths.isEmpty()) {
            newLength.unit = "mm"
        } else {
            val list = lengths.mapNotNull(Diary::length)
            newLength.unit = profile!!.lengthUnit
            newLength.latest = latest(list, newLength.unit)
            newLength.prev = diff(list, newLength.unit, "prev")
            newLength.prevPlus = sign(list, newLength.unit, "prev")
            newLength.recent = diff(list, newLength.unit, "recent")
            newLength.recentPlus = sign(list, newLength.unit, "recent")
            newLength.date = date(lengths[0].date)
        }

        age.value = profile!!.getAge(Calendar.getInstance().timeInMillis)
        weight.value = newWeight
        length.value = newLength
        createLineChart(chartWeight, weights.mapNotNull(Diary::weight).reversed())
        createLineChart(chartLength, lengths.mapNotNull(Diary::length).reversed())
    }

    private fun latest(dataList: List<Float>, suffix: String): String =
        convertUnit(dataList[0], suffix, false)

    private fun diff(list: List<Float>, suffix: String, mode: String): String {
        val diff = when (mode) {
            "prev" -> if (list.size < 2) list[0] - list.last() else list[0] - list[1]
            "recent" -> list[0] - list.last()
            else -> 0f
        }
        return when {
            diff > 0 -> "+ ${convertUnit(diff.absoluteValue, suffix, false)}"
            diff < 0 -> "- ${convertUnit(diff.absoluteValue, suffix, false)}"
            else -> convertUnit(diff.absoluteValue, suffix, false)
        }
    }
    private fun sign(list: List<Float>, suffix: String, mode: String): Boolean {
        val diff = when (mode) {
            "prev" -> if (list.size < 2) list[0] - list.last() else list[0] - list[1]
            "recent" -> list[0] - list.last()
            else -> 0f
        }
        return diff >= 0
    }

    private fun date(date: Long?): String {
        if (date == null) return "記録なし"
        return "最終記録日: ${convertLongToDateString(date)}"
    }

    private fun createLineChart(chart: LineChart, data: List<Float>) {
        chart.description.text = ""

        val values = arrayListOf<Entry>()
        data.forEachIndexed { index, value ->
            val entry = Entry(index.toFloat(), value)
            values.add(entry)
        }
        val valuesSet = LineDataSet(values, "")

        // 描画
        chart.data = LineData(valuesSet)
        chart.invalidate()

        // チャートの設定
        valuesSet.apply {
            lineWidth = 4f
            setDrawValues(false)
            circleRadius = 6f
            color = Color.parseColor("#c6aa80")
            setCircleColor(Color.parseColor("#c6aa80"))
        }

        chart.apply {
            isEnabled = true
            isDoubleTapToZoomEnabled = false
            setTouchEnabled(false)
            setDrawBorders(false)
            animateX(0)
            legend.isEnabled = false

            // 軸の非表示
            xAxis.isEnabled = false
            axisLeft.isEnabled = false
            axisRight. isEnabled = false
        }
    }

}