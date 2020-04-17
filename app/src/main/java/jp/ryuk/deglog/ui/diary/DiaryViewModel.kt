package jp.ryuk.deglog.ui.diary

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import kotlinx.coroutines.*

class DiaryViewModel(
    private val diaryDatabase: DiaryDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var diaries = MediatorLiveData<List<Diary>>()
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
            filteredDiaries.value = diaries.value
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