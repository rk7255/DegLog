package jp.ryuk.deglog.ui.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ryuk.deglog.database.*
import jp.ryuk.deglog.ui.data.ChartData
import jp.ryuk.deglog.ui.data.DisplayData
import jp.ryuk.deglog.utilities.Converter
import jp.ryuk.deglog.utilities.Utils
import jp.ryuk.deglog.utilities.getAge
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.min

class DashboardViewModel internal constructor(
    diaryRepository: DiaryRepository,
    profileRepository: ProfileRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {

    val allDiary = diaryRepository.getAllDiary()
    val allProfile = profileRepository.getAllProfile()
    val allTodo = todoRepository.getAllTodo()

    val nameListInDiary: List<String>
        get() = allDiary.value?.map(Diary::name)?.distinct() ?: listOf()
    private val nameListInProfile: List<String>
        get() = allProfile.value?.map(Profile::name)?.distinct() ?: listOf()
    var selected = MutableLiveData<String>()

    val unitWeight = MutableLiveData<String>()
    val unitLength = MutableLiveData<String>()

    /*
     * onClick
     */
    val clicked = MutableLiveData<Int?>()
    fun onClick(w: Int) {
        clicked.value = w
    }

    fun doneClick() {
        clicked.value = null
    }

    /*
     * TodoList
     */
    val todoList = MutableLiveData<List<Todo>>()
    val hasTodoList = MutableLiveData<Boolean>()

    fun setTodoList() {
        val newTodoList = allTodo.value?.filter { it.name == selected.value && !it.done }
        todoList.value = newTodoList
        hasTodoList.value = !newTodoList.isNullOrEmpty()
    }

    fun createTodo(name: String, text: String) {
        val newTodo = Todo(
            date = Calendar.getInstance().timeInMillis,
            name = name,
            todo = text,
            done = false
        )
        viewModelScope.launch { todoRepository.insert(newTodo) }
    }

    fun doneTodo(id: Long) {
        viewModelScope.launch { todoRepository.done(id, true) }
    }

    /*
     * Profile
     */
    var age = MutableLiveData<String>()
    var icon = MutableLiveData<Int>()
    val iconJsonString = MutableLiveData<String>()

    fun setProfile(context: Context) {
        if (allProfile.value.isNullOrEmpty()) return

        if (selected.value.isNullOrEmpty() || !nameListInDiary.contains(selected.value ?: ""))
            selected.value = nameListInProfile.first()

        val profile = allProfile.value?.find { it.name == selected.value } ?: Profile(name = "")
        age.value = profile.getAge(Calendar.getInstance().timeInMillis)

        val ic = profile.icon
        if (ic == null) {
            icon.value = Utils.iconSelector(context, profile.type)
        } else {
            iconJsonString.value = ic
        }
    }

    /*
     * Diary
     */
    val weightData = MutableLiveData<DisplayData>()
    val lengthData = MutableLiveData<DisplayData>()
    val free1Data = MutableLiveData<DisplayData>()
    val free2Data = MutableLiveData<DisplayData>()
    val weightChartData = MutableLiveData<List<ChartData>>()
    val lengthChartData = MutableLiveData<List<ChartData>>()
    val free1ChartData = MutableLiveData<List<ChartData>>()
    val free2ChartData = MutableLiveData<List<ChartData>>()
    val hasDiary = MutableLiveData<Boolean>()

    var free1Enabled = false
    var free2Enabled = false
    var free1Title = ""
    var free2Title = ""
    var free1Unit = ""
    var free2Unit = ""


    fun setDiary() {
        hasDiary.value = !allDiary.value.isNullOrEmpty()
        if (allDiary.value.isNullOrEmpty()) return

        if (selected.value.isNullOrEmpty() || !nameListInDiary.contains(selected.value ?: ""))
            selected.value = nameListInDiary.first()

        val diaryList = allDiary.value!!.filter { it.name == selected.value }

        // 体重
        val weightList = diaryList.mapNotNull(Diary::weight)
        val weightSubList = weightList.subList(0, min(7, weightList.size))
        val dateOfLatestWeight =
            if (weightList.isEmpty()) ""
            else Converter.longToDateString(diaryList.first { it.weight != null }.date)
        weightData.value = createDisplayData(weightSubList, dateOfLatestWeight, "w")

        val hasWeightDiaryList = diaryList.filter { it.weight != null }
        weightChartData.value = createChartData(hasWeightDiaryList, "w")

        // 体長
        val lengthList = diaryList.mapNotNull(Diary::length)
        val lengthSubList = lengthList.subList(0, min(7, lengthList.size))
        val dateOfLatestLength =
            if (lengthList.isEmpty()) ""
            else Converter.longToDateString(diaryList.first { it.length != null }.date)
        lengthData.value = createDisplayData(lengthSubList, dateOfLatestLength, "l")

        val hasLengthDiaryList = diaryList.filter { it.length != null }
        lengthChartData.value = createChartData(hasLengthDiaryList, "l")

        // FREE1
        if (free1Enabled) {
            val free1List = diaryList.mapNotNull(Diary::free1)
            val free1SubList = free1List.subList(0, min(7, free1List.size))
            val dateOfLatestFree1 =
                if (free1List.isEmpty()) ""
                else Converter.longToDateString(diaryList.first { it.free1 != null }.date)
            free1Data.value = createDisplayData(free1SubList, dateOfLatestFree1, "f1")

            val hasFree1DiaryList = diaryList.filter { it.free1 != null }
            free1ChartData.value = createChartData(hasFree1DiaryList, "f1")
        }

        // FREE2
        if (free2Enabled) {
            val free2List = diaryList.mapNotNull(Diary::free2)
            val free2SubList = free2List.subList(0, min(7, free2List.size))
            val dateOfLatestFree2 =
                if (free2List.isEmpty()) ""
                else Converter.longToDateString(diaryList.first { it.free2 != null }.date)
            free2Data.value = createDisplayData(free2SubList, dateOfLatestFree2, "f2")

            val hasFree2DiaryList = diaryList.filter { it.free2 != null }
            free2ChartData.value = createChartData(hasFree2DiaryList, "f2")
        }

    }

    private fun createDisplayData(subList: List<Float>, date: String, which: String): DisplayData {
        val unit = when (which) {
            "w" -> unitWeight.value ?: "g"
            "l" -> unitLength.value ?: "mm"
            "f1" -> free1Unit
            "f2" -> free2Unit
            else -> ""
        }

        return if (subList.isNotEmpty()) {
            DisplayData(
                date = date,
                latest = subList.first().exc(unit),
                prev = prev(subList, unit),
                isPlusPrev = isPlusPrev(subList),
                recent = recent(subList, unit),
                isPlusRecent = isPlusRecent(subList),
                unit = unit
            )
        } else {
            DisplayData()
        }
    }

    private fun createChartData(list: List<Diary>, which: String): List<ChartData> {
        return mutableListOf<ChartData>().apply {
            val cData = list.subList(0, min(7, list.size))
            cData.forEach {
                add(ChartData(
                    name = it.name,
                    date = it.date,
                    data = when (which) {
                        "w" -> it.weight
                        "l" -> it.length
                        "f1" -> it.free1
                        "f2" -> it.free2
                        else -> 0f
                    }!!
                ))
            }
        }
    }


    private fun Float.exc(unit: String): String =
        Converter.convertUnit(this, unit, false)

    private fun prev(values: List<Float>, unit: String): String =
        if (values.size <= 1) onSign(0f, unit) else onSign(values[0] - values[1], unit)

    private fun isPlusPrev(values: List<Float>): Boolean =
        if (values.size <= 1) true else (values[0] - values[1] >= 0)

    private fun recent(values: List<Float>, unit: String): String =
        onSign(values[0] - values.last(), unit)

    private fun isPlusRecent(values: List<Float>): Boolean =
        values[0] - values.last() >= 0

    private fun onSign(num: Float, unit: String): String {
        val n = when (unit) {
            "g" -> Converter.convertUnit(num, "g", false)
            "kg" -> Converter.convertUnit(num, "kg", false)
            "mm" -> Converter.convertUnit(num, "mm", false)
            "cm" -> Converter.convertUnit(num, "cm", false)
            "m" -> Converter.convertUnit(num, "m", false)
            else -> Converter.convertUnit(num, "g", false)
        }
        return if (num >= 0) "+ $n" else "- $n"
    }


}