package jp.ryuk.deglog.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
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

    val allDiary: LiveData<List<Diary>> = diaryRepository.getAllDiary()
    val allProfile: LiveData<List<Profile>> = profileRepository.getAllProfile()
    val allTodo: LiveData<List<Todo>> = todoRepository.getAllTodo()
    val nameList: List<String> get() = allDiary.value?.map(Diary::name)?.distinct() ?: listOf()
    var selected = ""

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
        val newTodoList = allTodo.value?.filter { it.name == selected && !it.done }
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

    fun setProfile(context: Context) {
        val profile = allProfile.value?.find { it.name == selected } ?: Profile(name = "")
        age.value = profile.getAge(Calendar.getInstance().timeInMillis)
        icon.value = Utils.iconSelector(context, profile.type)
    }

    /*
     * Diary
     */
    val weightData = MutableLiveData<DisplayData>()
    val lengthData = MutableLiveData<DisplayData>()
    val weightDataList = MutableLiveData<List<ChartData>>()
    val lengthDataList = MutableLiveData<List<ChartData>>()

    fun setDiary() {
        if (allDiary.value.isNullOrEmpty()) return
        if (selected.isEmpty() || !nameList.contains(selected))
            selected = nameList.first()

        val diaryList = allDiary.value!!.filter { it.name == selected }

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
                latest = wSubList.first().toString(),
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
                latest = lSubList.first().toString(),
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
        if (num >= 0) "+ ${num.absoluteValue}" else "- ${num.absoluteValue}"

}