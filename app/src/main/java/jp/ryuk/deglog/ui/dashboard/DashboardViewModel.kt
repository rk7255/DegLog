package jp.ryuk.deglog.ui.dashboard

import android.graphics.Color
import android.util.Log
import androidx.lifecycle.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.absoluteValue

class DashboardViewModel(
    private val diaryDatabase: DiaryDao,
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

    var todoList = MediatorLiveData<List<Todo>>()
    var hasTodo = MutableLiveData<Boolean>()
    var hasNotify = MutableLiveData<Boolean>()

    lateinit var chartWeight: LineChart
    lateinit var chartLength: LineChart

    fun changeDashboard() {
        todoList.value = listOf()
        hasTodo.value = false
        hasNotify.value = false

        val filtered = diaries.value!!.filter { it.name == selected.value!! }
        val profile = profiles.value!!.find { it.name == selected.value }

        var weights = filtered.filter { it.weight != null }
        if (weights.size >= 7) weights = weights.subList(0, 7)
        var lengths = filtered.filter { it.length != null }
        if (lengths.size >= 7) lengths = lengths.subList(0, 7)

        val todos = filtered.filter { it.todo != null && it.success == false }
        if (todos.isNotEmpty()) {
            val list = mutableListOf<Todo>()
            todos.forEach {
                val alert = it.success == false && hasAlert(it.date)
                hasNotify.value = alert

                val newTodo = Todo(
                    id = it.id,
                    time = convertLongToDateStringRelative(it.date),
                    todo = it.todo!!,
                    success = it.success!!,
                    alert = alert
                )
                list.add(newTodo)
            }
            hasTodo.value = true
            todoList.value = list
        }

        val newWeight = Dashboard()
        if (weights.isNotEmpty()) {
            val weightList = weights.mapNotNull(Diary::weight)
            newWeight.unit = profile!!.weightUnit
            newWeight.latest = latest(weightList, newWeight.unit)
            newWeight.prev = diff(weightList, newWeight.unit, "prev")
            newWeight.prevPlus = sign(weightList, "prev")
            newWeight.recent = diff(weightList, newWeight.unit, "recent")
            newWeight.recentPlus = sign(weightList, "recent")
            newWeight.date = date(weights[0].date)
        }

        val newLength = Dashboard()
        if (lengths.isNotEmpty()) {
            val lengthList = lengths.mapNotNull(Diary::length)
            newLength.unit = profile!!.lengthUnit
            newLength.latest = latest(lengthList, newLength.unit)
            newLength.prev = diff(lengthList, newLength.unit, "prev")
            newLength.prevPlus = sign(lengthList, "prev")
            newLength.recent = diff(lengthList, newLength.unit, "recent")
            newLength.recentPlus = sign(lengthList, "recent")
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
    private fun sign(list: List<Float>, mode: String): Boolean {
        val diff = when (mode) {
            "prev" -> if (list.size < 2) list[0] - list.last() else list[0] - list[1]
            "recent" -> list[0] - list.last()
            else -> 0f
        }
        return diff >= 0
    }

    private fun date(date: Long?): String {
        if (date == null) return "記録なし"
        return convertLongToDateString(date)
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

    fun newTodo(text: String) {
        val newDiary = Diary(
            name = selected.value!!,
            todo = text,
            success = false
        )
        Log.d(deg, "insert $newDiary")
        viewModelScope.launch { insert(newDiary) }
    }

    private suspend fun insert(diary: Diary) {
        withContext(Dispatchers.IO) {
            diaryDatabase.insert(diary)
        }
    }

    fun deleteTodo(id: Long) {
        viewModelScope.launch { deleteById(id) }
    }

    private suspend fun deleteById(id: Long) {
        withContext(Dispatchers.IO) {
            diaryDatabase.deleteById(id)
        }
    }
}