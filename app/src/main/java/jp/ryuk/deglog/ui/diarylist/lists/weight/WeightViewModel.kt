package jp.ryuk.deglog.ui.diarylist.lists.weight

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import kotlinx.coroutines.*


class WeightViewModel(
    private val selectedName: String,
    private val diaryDatabase: DiaryDao
) : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _diaries = listOf<Diary>()
    var diaries = MediatorLiveData<List<Diary>>()

    init {
        uiScope.launch {
            _diaries = getDiariesAtName(selectedName)
            diaries.value = _diaries.filter { it.weight != null }
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
    private suspend fun getDiariesAtName(name: String): List<Diary> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiariesAtName(name)
        }
    }

}
