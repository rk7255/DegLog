package jp.ryuk.deglog.ui.diarydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import kotlinx.coroutines.*

class DiaryDetailViewModel(
    private val diaryKey: Long,
    private val selectedName: String,
    private val diaryDatabase: DiaryDao
) : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var diaries = MediatorLiveData<List<Diary>>()
    private var _diaryPosition = MediatorLiveData<Int>()
    val diaryPosition: LiveData<Int>
        get() = _diaryPosition

    init {
        uiScope.launch {
            diaries.value = getDiaries(selectedName).reversed()
            val ids = diaries.value!!.map(Diary::id)
            _diaryPosition.value = ids.indexOf(diaryKey)
        }
    }

    /**
     * onClick
     */
    fun saveDiary(position: Int) {
        val diary = diaries.value!![position]
        val newDiary = Diary()
        newDiary.id = diary.id

        updateDiary(newDiary)
    }

    fun deleteDiary(position: Int): String {
        val diary = diaries.value!![position]
        val id = diary.id
        deleteById(id)
        return diary.name
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }


}
