package jp.ryuk.deglog.ui.dashboard

import android.app.Application
import androidx.lifecycle.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.utilities.convertLongToDateStringRelative
import kotlinx.coroutines.*
import kotlin.math.absoluteValue

class DashboardViewModel(
    private val diaryDatabase: DiaryDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var diaries = listOf<Diary>()
    var names = listOf<String>()
    var filteredDiaries = MediatorLiveData<List<Diary>>()

    var selectedFilter = ""
    lateinit var weightChart: LineChart
    lateinit var lengthChart: LineChart

    /**
     * Initialize
     */
    init {
        initialize()
    }

    private fun initialize() {
        uiScope.launch {
            names = getNames()
            diaries = getDiaries()
            _initialized.value = true
        }
    }

    private var _initialized = MutableLiveData<Boolean>()
    val initialized: LiveData<Boolean>
        get() = _initialized
    fun doneInitialized() {
        _initialized.value = false
    }

    /**
     * Dashboard
     */
    private var weights = listOf<Float>()
    private var lengths = listOf<Float>()
    var weight = MediatorLiveData<Dashboard>()
    var length = MediatorLiveData<Dashboard>()

    private var _changeDashboard = MutableLiveData<Boolean>()
    val changeDashboard: LiveData<Boolean>
        get() = _changeDashboard
    fun doneChangeDashboard() {
        _changeDashboard.value = false
    }

    private fun changeDashboard(
        listOfWeight: List<Float>, dateOfWeight: Long,
        listOfLength: List<Float>, dateOfLength: Long) {

        val wgt = listEmptyCheck(listOfWeight)
        val len = listEmptyCheck(listOfLength)

        weight.value = newDashboard(wgt, dateOfWeight, "g")
        length.value = newDashboard(len, dateOfLength, "mm")

        _changeDashboard.value = true
    }

    private fun newDashboard(dataList: List<Float>, date: Long, suffix: String): Dashboard {
        val dashboard = Dashboard()
        dashboard.latest = latest(dataList, suffix)
        dashboard.date = dateFormatter(date)
        dashboard.prev = previous(dataList, suffix)
        dashboard.diff = difference(dataList)
        return dashboard
    }

    private fun listEmptyCheck(dataList: List<Float>): List<Float> {
        return when {
            dataList.isEmpty() -> listOf(0f, 0f)
            dataList.size == 1 -> dataList.plus(dataList)
            else -> dataList
        }
    }
    private fun dateFormatter(date: Long): String {
        return if (date == 0L) "no data" else convertLongToDateStringRelative(date)
    }

    private fun latest(dataList: List<Float>, suffix: String): String {
        return "${dataList[0]} $suffix"
    }

    private fun previous(dataList: List<Float>, suffix: String): String {
        val diff = (dataList[0] - dataList[1])
        return when {
            diff > 0 -> "+ ${diff.absoluteValue} $suffix"
            diff < 0 -> "- ${diff.absoluteValue} $suffix"
            else -> "0 $suffix"
        }
    }

    private fun difference(dataList: List<Float>): String {
        val percent = diffPercent(dataList.first(), dataList.last())

        return when {
            percent.absoluteValue < 0.02 -> "flat"
            percent > 0 -> "up"
            percent < 0 -> "down"
            else -> "flat"
        }
    }

    private fun diffPercent(first: Float, last: Float): Double {
        return (first.toDouble() / last.toDouble()) - 1
    }

    /**
     * Chips Click Event
     */
    fun changeFilterNames(name: String, id: Int) {
        uiScope.launch {
            if (id >= 0) {
                filteredDiaries.value = diaries.filter { it.name == name }

                val weightList = filteredDiaries.value!!.mapNotNull(Diary::weight)
                val lengthList = filteredDiaries.value!!.mapNotNull(Diary::length)
                weights = if (weightList.size >= 7) weightList.subList(0, 7) else weightList
                lengths = if (lengthList.size >= 7) lengthList.subList(0, 7) else lengthList

                val dateOfWeight = getDateOfWeightLatest(name)
                val dateOfLength = getDateOfLengthLatest(name)
                createLineChart(weightChart, weights.reversed())
                createLineChart(lengthChart, lengths.reversed())

                changeDashboard(
                    weights, dateOfWeight,
                    lengths, dateOfLength)
            }
        }
    }



    /**
     * Chart
     */
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

    /**
     * onClick
     */
    private var _navigateToDetail = MutableLiveData<Int?>()
    val navigateToDetail: LiveData<Int?>
        get() = _navigateToDetail
    fun navigateToDetail(key: Int) {
        _navigateToDetail.value = key
    }
    fun doneNavigateToDetail() {
        _navigateToDetail.value = null
    }

    /**
     * Database
     */
    private suspend fun  getDiaries(): List<Diary> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiaries()
        }
    }

    private suspend fun getDiariesAtName(name: String): List<Diary> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiariesAtName(name)
        }
    }

    private suspend fun getDateOfWeightLatest(name: String): Long {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDateOfWeightLatest(name)
        }
    }

    private suspend fun getDateOfLengthLatest(name: String): Long {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDateOfLengthLatest(name)
        }
    }

    private suspend fun getNames(): List<String> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getNames()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}