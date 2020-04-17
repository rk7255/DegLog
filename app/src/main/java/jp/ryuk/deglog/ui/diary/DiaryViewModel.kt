package jp.ryuk.deglog.ui.diary

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import kotlinx.coroutines.*

class DiaryViewModel(
    private val diaryDatabase: DiaryDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var diaries = MediatorLiveData<List<Diary>>()
    var names = listOf<String>()
    var filteredDiaries = MediatorLiveData<List<Diary>>()

    init {
        initialize()
    }

    private fun initialize() {
        uiScope.launch {
            names = getNames()
            Log.d("DEBUG", "$names")
            diaries.value = getDiaries()
            _initialized.value = true
        }
    }

    fun changeFilterNames(name: String, id: Int) {
        uiScope.launch {
            if (id == -1) {
                filteredDiaries.value = diaries.value
            } else {
                filteredDiaries.value = diaries.value?.filter { it.name == name }
            }
        }
    }

    private var _initialized = MutableLiveData<Boolean>()
    val initialized: LiveData<Boolean>
        get() = _initialized
    fun doneInitialized() {
        _initialized.value = false
    }

    /**
     * Database
     */
    private suspend fun  getDiaries(): List<Diary> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiaries()
        }
    }

    private suspend fun getDiariesAtName(name: String): List<Diary> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiariesAtName(name)
        }
    }

    private suspend fun getNames(): List<String> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getNames()
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}