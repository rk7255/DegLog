package jp.ryuk.deglog.ui.diarydetail.details

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import kotlinx.coroutines.*


class WeightViewModel(
    private val detailType: Int,
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
     * Database
     */
    private suspend fun  getDiariesAtName(name: String): List<Diary> {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiariesAtName(name)
        }
    }

}
