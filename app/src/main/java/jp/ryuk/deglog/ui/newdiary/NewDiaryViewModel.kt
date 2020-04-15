package jp.ryuk.deglog.ui.newdiary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.convertStringToInt
import kotlinx.coroutines.*


class NewDiaryViewModel(
    private val diaryDatabase: DiaryDao,
    private val profileDatabase: ProfileDao
) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var name: String = ""
    var weight: String? = null
    var length: String? = null
    var memo: String? = null

    /**
     * Initialize
     */
    init {
        initialize()
    }

    private fun initialize() {
        uiScope.launch {

        }
    }

    /**
     * onClick
    */
    fun onSubmit() {
        if (isInputDataValid()) insertNewDiary()
    }

    private fun isInputDataValid(): Boolean {
        val isValid = name.isNotEmpty()
        _submitError.value = !isValid
        return isValid
    }

    fun onCancel() {
        _navigateToDiary.value = true
    }

    /**
     *  LiveData
     */
    private var _navigateToDiary = MutableLiveData<Boolean>()
    val navigateToDiary: LiveData<Boolean>
        get() = _navigateToDiary
    fun doneNavigateToDiary() {
        _navigateToDiary.value = false
    }

    private var _submitError = MutableLiveData<Boolean>()
    val submitError: LiveData<Boolean>
        get() = _submitError


    /**
     * Database
     */
    private suspend fun insert(diary: Diary) {
        withContext(Dispatchers.IO) {
            diaryDatabase.insert(diary)
        }
    }

    private fun insertNewDiary() {
        uiScope.launch {
            val newDiary = Diary()
            newDiary.name = name
            newDiary.weight = convertStringToInt(weight)
            newDiary.length = convertStringToInt(length)
            newDiary.memo = if (memo.isNullOrEmpty()) null else memo
            Log.d("DEBUG", "Insert New Diary -> $newDiary")
            insert(newDiary)
            _navigateToDiary.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}