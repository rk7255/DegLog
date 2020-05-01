package jp.ryuk.deglog.ui.diarydetail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.*
import kotlinx.coroutines.*
import java.lang.StringBuilder
import java.time.LocalDate
import java.time.Period

@RequiresApi(Build.VERSION_CODES.O)
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
            val profile = getProfile(selectedName)
            val detailList = mutableListOf<Detail>()

            diaries.forEach { diary ->
                val detail = Detail()
                detail.id = diary.id
                detail.date = diary.date
                detail.name = diary.name
                diary.weight?.let { detail.weight = convertUnit(it, profile.weightUnit) }
                diary.length?.let { detail.length = convertUnit(it, profile.lengthUnit) }
                detail.memo = diary.memo
                profile.birthday?.let { detail.age = getAge(it, diary.date) }
                detailList.add(detail)
            }

            details.value = detailList

            val ids = diaries.map(Diary::id)
            _diaryPosition.value = ids.indexOf(diaryKey)
        }
    }

    private fun getAge(birthday: Long, date: Long): String {
        val from = LocalDate.of(birthday.getYear(), birthday.getMonth(), birthday.getDayOfMonth())
        val to = LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth())
        val diff = Period.between(from, to)

        val age = StringBuilder()
        if (diff.years > 0) age.append("${diff.years}歳 ")
        if (diff.months > 0) age.append("${diff.months}ヶ月 ")
        if (diff.days > 0) age.append("${diff.days}日")

        return age.toString()
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

    private suspend fun getProfile(name: String): Profile {
        return withContext(Dispatchers.IO) {
            profileDatabase.getProfile(name)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
