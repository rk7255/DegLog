package jp.ryuk.deglog.ui.diarylist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import kotlinx.coroutines.*

class DiaryListViewModel(
    private val selectedName: String,
    private val diaryDatabase: DiaryDao,
    private val profileDatabase: ProfileDao
) : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _initialized = MutableLiveData<Boolean>()
    val initialized: LiveData<Boolean> get() = _initialized

    var diaries = MediatorLiveData<List<Diary>>()
    var suffixWeight = MediatorLiveData<String>()
    var suffixLength = MediatorLiveData<String>()

    var position = MediatorLiveData<Int>()

    init {
        initialize()
    }

    private fun initialize() {
        uiScope.launch {
            diaries.value = getDiariesAtName(selectedName)

            val profile = getProfile(selectedName)
            suffixWeight.value = profile.weightUnit
            suffixLength.value = profile.lengthUnit

            _initialized.value = true
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