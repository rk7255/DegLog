package jp.ryuk.deglog.ui.diarydetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.tag
import kotlinx.coroutines.*
import java.time.chrono.ChronoPeriod
import java.time.temporal.ChronoUnit
import java.util.*

class DiaryDetailViewModel(
    private val diaryKey: Long,
    private val selectedName: String,
    private val diaryDatabase: DiaryDao,
    private val profileDatabase: ProfileDao
) : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var diaries = listOf<Diary>()
    private var _diaryPosition = MediatorLiveData<Int>()
    val diaryPosition: LiveData<Int>
        get() = _diaryPosition
    var details = MediatorLiveData<List<Detail>>()


    init {
        initialize()
    }

    private fun initialize() {
        uiScope.launch {
            diaries = getDiaries(selectedName).reversed()
            val birthday = getBirthday(selectedName)
            val detailList = mutableListOf<Detail>()

            diaries.forEach { diary ->
                val detail = Detail()
                detail.id = diary.id
                detail.date = diary.date
                detail.name = diary.name
                detail.weight = diary.weight
                detail.length = diary.length
                detail.memo = diary.memo
                birthday?.let { detail.age = getAge(birthday, diary.date) }
                detailList.add(detail)
            }

            details.value = detailList

            val ids = diaries.map(Diary::id)
            _diaryPosition.value = ids.indexOf(diaryKey)
        }
    }

    private fun getAge(birthday: Long, date: Long): String {
        val diffDays = (date - birthday) / (1000 * 60 * 60 * 24)

        return "$diffDays 日"
    }

    /**
     * onClick
     */
    fun editDiary(position: Int): Long {
        return details.value!![position].id
    }

    fun deleteDiary(position: Int): String {
        val detail = details.value!![position]
        val id = detail.id
        deleteById(id)
        return detail.name
    }

    /**
     * LiveData
     */
    private var _navigateToDiaryDetail = MutableLiveData<Long?>()
    val navigateToDiaryDetail: LiveData<Long?>
        get() = _navigateToDiaryDetail
    fun doneNavigateToDiary() {
        _navigateToDiaryDetail.value = null
    }

    /**
     * Database
     */
    private suspend fun getDiary(key: Long): Diary {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiary(key)
        }
    }
    private suspend fun getDiaries(selectedName: String): List<Diary> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiariesAtName(selectedName)
        }
    }

    private suspend fun update(diary: Diary) {
        withContext(Dispatchers.IO) {
            diaryDatabase.update(diary)
        }
    }

    private fun updateDiary(diary: Diary) {
        uiScope.launch {
            update(diary)
        }
    }

    private suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) {
            diaryDatabase.deleteById(id)
        }
    }

    private fun deleteById(id: Long) {
        uiScope.launch {
            delete(id)
        }
    }

    private suspend fun getBirthday(name: String): Long? {
        return withContext(Dispatchers.IO) {
            profileDatabase.getBirthday(name)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
