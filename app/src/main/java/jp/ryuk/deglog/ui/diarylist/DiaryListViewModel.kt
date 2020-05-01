package jp.ryuk.deglog.ui.diarylist

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.convertLongToDateStringOutYear
import jp.ryuk.deglog.utilities.convertUnit
import jp.ryuk.deglog.utilities.getMonth
import kotlinx.coroutines.*
import kotlin.collections.List

class DiaryListViewModel(
    private val selectedName: String,
    private val diaryDatabase: DiaryDao,
    private val profileDatabase: ProfileDao
) : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _diaries = listOf<Diary>()
    var diaries = MediatorLiveData<List<Diary>>()

    init {
        initialize()
    }

    private fun initialize() {
        uiScope.launch {
            _diaries = getDiariesAtName(selectedName)
            diaries.value = _diaries
        }
    }

    /**
     * Database
     */
    private suspend fun getDiariesAtName(name: String): List<Diary> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiariesAtName(name)
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