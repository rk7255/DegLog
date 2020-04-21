package jp.ryuk.deglog.ui.diary

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.utilities.convertLongToDateString
import jp.ryuk.deglog.utilities.convertLongToDateStringRelative
import kotlinx.coroutines.*
import kotlin.math.absoluteValue

class DiaryViewModel(
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
                changeDashboard(weights, dateOfWeight, lengths, dateOfLength)
            }
        }
    }

    /**
     * Dashboard
     */
    private var weights = listOf<Int>()
    var weightLatest = MediatorLiveData<String>()
    var weightLatestDate = MediatorLiveData<String>()
    var weightPrev = MediatorLiveData<String>()
    var weightDiff = MutableLiveData<String>()

    private var lengths = listOf<Int>()
    var lengthLatest = MediatorLiveData<String>()
    var lengthLatestDate = MediatorLiveData<String>()
    var lengthPrev = MediatorLiveData<String>()
    var lengthDiff = MutableLiveData<String>()

    private var _changeDashboard = MutableLiveData<Boolean>()
    val changeDashboard: LiveData<Boolean>
        get() = _changeDashboard
    fun doneChangeDashboard() {
        _changeDashboard.value = false
    }

    private fun changeDashboard(listOfWeight: List<Int>, dateOfWeight: Long, listOfLength: List<Int>, dateOfLength: Long) {
        val wgt = listEmptyCheck(listOfWeight)
        val len = listEmptyCheck(listOfLength)

        weightLatest.value = latest(wgt, "g")
        weightLatestDate.value = dateFormatter(dateOfWeight)
        weightPrev.value = previous(wgt, "g")
        weightDiff.value = difference(wgt)

        lengthLatest.value = latest(len, "mm")
        lengthLatestDate.value = dateFormatter(dateOfLength)
        lengthPrev.value = previous(len, "mm")
        lengthDiff.value = difference(len)

        _changeDashboard.value = true
    }

    private fun listEmptyCheck(dataList: List<Int>): List<Int> {
        return when {
            dataList.isEmpty() -> listOf(0, 0)
            dataList.size == 1 -> dataList.plus(dataList)
            else -> dataList
        }
    }

    private fun dateFormatter(date: Long): String {
        return if (date == 0L) "no data" else convertLongToDateStringRelative(date)
    }

    private fun latest(dataList: List<Int>, suffix: String): String {
        return "${dataList[0]} $suffix"
    }

    private fun previous(dataList: List<Int>, suffix: String): String {
        val diff = (dataList[0] - dataList[1])
        return when {
            diff > 0 -> "+ ${diff.absoluteValue} $suffix"
            diff < 0 -> "- ${diff.absoluteValue} $suffix"
            else -> "0 $suffix"
        }
    }

    private fun difference(dataList: List<Int>): String {
        val diff = dataList.first() - dataList.last()
        return when {
            diff > 0 -> "up"
            diff < 0 -> "down"
            else -> "flat"
        }
    }

    /**
     * Chart
     */
    private fun createLineChart(chart: LineChart, data: List<Int>) {
        chart.setNoDataText("データがありません")
        chart.description.text = ""

        val values = arrayListOf<Entry>()
        data.forEachIndexed { index, value ->
            val entry = Entry(index.toFloat(), value.toFloat())
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
            circleRadius = 2f
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