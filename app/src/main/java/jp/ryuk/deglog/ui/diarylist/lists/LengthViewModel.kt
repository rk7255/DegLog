package jp.ryuk.deglog.ui.diarylist.lists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import kotlinx.coroutines.*

class LengthViewModel(
    private val selectedName: String,
    private val diaryDatabase: DiaryDao
) : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var diaries = MediatorLiveData<List<Diary>>()


    init {
        uiScope.launch {
            diaries.value = getDiariesAtName(selectedName)
        }
    }

    /**
     * onClick
     */
    fun onClickDiary(key: Long) {
        _navigateToDiaryDetail.value = key
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
    private suspend fun  getDiariesAtName(name: String): List<Diary> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiariesAtName(name)
        }
    }

}
