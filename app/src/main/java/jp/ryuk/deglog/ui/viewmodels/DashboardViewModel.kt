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
import kotlin.math.absoluteValue
import kotlin.math.min

class DashboardViewModel internal constructor(
    diaryRepository: DiaryRepository,
    profileRepository: ProfileRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {

    val allDiary = diaryRepository.getAllDiary()
    val allProfile = profileRepository.getAllProfile()
    val allTodo = todoRepository.getAllTodo()

    val nameListInDiary: List<String> get() = allDiary.value?.map(Diary::name)?.distinct() ?: listOf()
    private val nameListInProfile: List<String> get() = allProfile.value?.map(Profile::name)?.distinct() ?: listOf()
    var selected = MutableLiveData<String>()

    /*
     * onClick
     */
    val clicked = MutableLiveData<Int?>()
    fun onClick(w: Int) { clicked.value = w }
    fun doneClick() { clicked.value = null }

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
    val weightDataList = MutableLiveData<List<ChartData>>()
    val lengthDataList = MutableLiveData<List<ChartData>>()
    val hasDiary = MutableLiveData<Boolean>()

    fun setDiary() {
        hasDiary.value = !allDiary.value.isNullOrEmpty()
        if (allDiary.value.isNullOrEmpty()) return

        if (selected.value.isNullOrEmpty() || !nameListInDiary.contains(selected.value ?: ""))
            selected.value = nameListInDiary.first()

        val diaryList = allDiary.value!!.filter { it.name == selected.value }

        // 体重
        val wChartData = mutableListOf<ChartData>().apply {
            val l = diaryList.filter { it.weight != null }
            val cData = l.subList(0, min(7, l.size))
            cData.forEach {
                add(ChartData(
                    name = it.name,
                    date = it.date,
                    data = it.weight!!
                ))
            }
        }
        weightDataList.value = wChartData

        val wList = diaryList.mapNotNull(Diary::weight)
        val wSubList = wList.subList(0, min(7, wList.size))

        weightData.value = if (wSubList.isNotEmpty()) {
            DisplayData(
                date = Converter.longToDateString(diaryList.first { it.weight != null }.date),
                latest = wSubList.first().toInt().toString(),
                prev = prev(wSubList),
                isPlusPrev = isPlusPrev(wSubList),
                recent = recent(wSubList),
                isPlusRecent = isPlusRecent(wSubList),
                unit = "g"
            )
        } else {
            DisplayData()
        }

        // 体長

        val lChartData = mutableListOf<ChartData>().apply {
            val l = diaryList.filter { it.length != null }
            val cData = l.subList(0, min(7, l.size))
            cData.forEach {
                add(ChartData(
                    name = it.name,
                    date = it.date,
                    data = it.length!!
                ))
            }
        }
        lengthDataList.value = lChartData

        val lList = diaryList.mapNotNull(Diary::length)
        val lSubList = lList.subList(0, min(7, lList.size))

        lengthData.value = if (lSubList.isNotEmpty()) {
            DisplayData(
                date = Converter.longToDateString(diaryList.first { it.length != null }.date),
                latest = lSubList.first().toInt().toString(),
                prev = prev(lSubList),
                isPlusPrev = isPlusPrev(lSubList),
                recent = recent(lSubList),
                isPlusRecent = isPlusRecent(lSubList),
                unit = "mm"
            )
        } else {
            DisplayData()
        }
    }

    private fun prev(values: List<Float>): String =
        if (values.size <= 1) "0" else onSign(values[0] - values[1])

    private fun isPlusPrev(values: List<Float>): Boolean =
        if (values.size <= 1) true else (values[0] - values[1] >= 0)

    private fun recent(values: List<Float>): String =
        onSign(values[0] - values.last())

    private fun isPlusRecent(values: List<Float>): Boolean =
        values[0] - values.last() >= 0

    private fun onSign(num: Float): String =
        if (num >= 0) "+ ${num.absoluteValue.toInt()}" else "- ${num.absoluteValue.toInt()}"

}