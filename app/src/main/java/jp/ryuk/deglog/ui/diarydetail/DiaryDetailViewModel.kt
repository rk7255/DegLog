package jp.ryuk.deglog.ui.diarydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import kotlinx.coroutines.*

class DiaryDetailViewModel(
    private val diaryKey: Long,
    private val diaryDatabase: DiaryDao
) : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var diary = MediatorLiveData<Diary>()

    init {
        uiScope.launch {
            diary.value = getDiary(diaryKey)
        }
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
    private suspend fun  getDiary(key: Long): Diary {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiary(key)
        }
    }

}
