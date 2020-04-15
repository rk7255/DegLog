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
import kotlin.random.Random

class DiaryViewModel(
    private val diaryDatabase: DiaryDao,
    application: Application
) : AndroidViewModel(application) {

    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var diaries = MediatorLiveData<List<Diary>>()

    init {
        initialize()
    }

    private fun initialize() {
        uiScope.launch {
            diaries.value = getDiaries()
        }
    }


    /**
     * onClick DEBUG
     */
    fun onAddDiary() {
        uiScope.launch {
            val newDiary = Diary()
            newDiary.name = "natsu"
            newDiary.weight = 200 + Random.nextInt(0, 50)
            newDiary.length = 100+ Random.nextInt(0, 20)
            newDiary.memo = "debug"
            Log.d("DEBUG", "Diary Insert -> $newDiary")
            insert(newDiary)
            initialize()
        }
    }

    fun onClear() {
        uiScope.launch {
            clear()
            initialize()
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

    private suspend fun insert(diary: Diary) {
        withContext(Dispatchers.IO) {
            diaryDatabase.insert(diary)
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            diaryDatabase.clear()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}