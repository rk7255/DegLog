package jp.ryuk.deglog.ui.diarydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import kotlinx.coroutines.*

class DiaryDetailViewModel(
    private val diaryId: Long,
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
                detail.weight = diary.convertWeightUnit(profile.weightUnit, true)
                detail.length = diary.convertLengthUnit(profile.lengthUnit, true)
                detail.memo = diary.memo
                detail.age = profile.getAge(diary.date)
                detailList.add(detail)
            }

            details.value = detailList

            val ids = diaries.map(Diary::id)
            _diaryPosition.value = ids.indexOf(diaryId)
        }
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
    private suspend fun getDiaries(selectedName: String): List<Diary> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiariesAtName(selectedName)
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
