package jp.ryuk.deglog.ui.newdiary

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.convertLongToDateStringInTime
import jp.ryuk.deglog.utilities.convertStringToFloat
import jp.ryuk.deglog.utilities.convertYMDToLong
import kotlinx.coroutines.*
import java.util.*
import kotlin.random.Random


class NewDiaryViewModel(
    diaryId: Long,
    private val selectedName: String,
    private val diaryDatabase: DiaryDao,
    private val profileDatabase: ProfileDao
) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var names = listOf<String>()
    var id: Long = -1L
    var date: Long = 0L
    var dateOfString = ""
    var name: String = selectedName
    var weight: String?= null
    var length: String? = null
    var memo: String? = null

    var weightUnit = MediatorLiveData<String>()
    var lengthUnit = MediatorLiveData<String>()

    /**
     * Initialize
     */
    init {
        id = diaryId
        if (id == -1L) initialize() else initializeEdit()
    }

    private fun initialize() {
        uiScope.launch {
            val profile = getProfile(selectedName)
            weightUnit.value = profile.weightUnit
            lengthUnit.value = profile.lengthUnit

            names = getNames()
            date = Calendar.getInstance().timeInMillis
            dateOfString = convertLongToDateStringInTime(date)
            _initialized.value = true
        }
    }

    private fun initializeEdit() {
        uiScope.launch {
            val profile = getProfile(selectedName)
            weightUnit.value = profile.weightUnit
            lengthUnit.value = profile.lengthUnit

            val diary = getDiary(id)
            name = diary.name
            date = diary.date
            dateOfString = convertLongToDateStringInTime(date)
            weight = diary.weight?.toString()
            length = diary.length?.toString()
            memo = diary.memo
            _initialized.value = true
        }
    }


    /**
     * onClick
    */
    fun onSubmit() {
        if (id == -1L) {
            if (isInputDataValid()) insertNewDiary()
        } else {
            if (isInputDataValid()) updateDiary()
        }

    }

    private fun isInputDataValid(): Boolean {
        val isValid = name.isNotEmpty()
        _submitError.value = !isValid
        return isValid
    }

    fun onCancel() {
        _backToDiary.value = true
    }

    fun onDate() {
        _getCalendar.value = true
    }

    /**
     * DEBUG
     */
    fun onClear() {
        uiScope.launch {
            clear(selectedName)
            _navigateToDiary.value = true
        }
    }

    fun onAddDummy() {
        uiScope.launch {

            for (m in 1..3) {
                for (d in 1..5) {
                    val loop = Random.nextInt(0, 3)
                    for (i in 0..loop) {
                        val newDiary = Diary()
                        newDiary.date = convertYMDToLong(2020, m, d)
                        newDiary.name = name
                        newDiary.weight = 250f + loop * Random.nextInt(1, 15)
                        newDiary.length = 150f + loop * Random.nextInt(1, 15)
                        newDiary.memo = "DUMMY DUMMY"
                        insert(newDiary)
                    }
                }
            }
            _navigateToDiary.value = true
        }
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

    private var _navigateToDiaryDetail = MutableLiveData<Boolean>()
    val navigateToDiaryDetail: LiveData<Boolean>
        get() = _navigateToDiaryDetail
    fun doneNavigateToDiaryDetail() {
        _navigateToDiaryDetail.value = false
    }

    private var _backToDiary = MutableLiveData<Boolean>()
    val backToDiary: LiveData<Boolean>
        get() = _backToDiary
    fun doneBackToDiary() {
        _backToDiary.value = false
    }

    private var _submitError = MutableLiveData<Boolean>()
    val submitError: LiveData<Boolean>
        get() = _submitError

    private var _initialized = MutableLiveData<Boolean>()
    val initialized: LiveData<Boolean>
        get() = _initialized
    fun doneInitialized() {
        _initialized.value = false
    }

    private var _getCalendar = MutableLiveData<Boolean>()
    val getCalendar: LiveData<Boolean>
        get() = _getCalendar
    fun doneGetCalendar(time: Long) {
        date = time
        dateOfString = convertLongToDateStringInTime(date)
        _initialized.value = true
        _getCalendar.value = false
    }

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
            newDiary.date = date
            newDiary.weight = convertStringToFloat(weight, weightUnit.value!!)
            newDiary.length = convertStringToFloat(length, lengthUnit.value!!)
            newDiary.memo = if (memo.isNullOrEmpty()) null else memo
            Log.d("DEBUG", "Insert New Diary -> $newDiary")
            insert(newDiary)
            _navigateToDiary.value = true
        }
    }

    private suspend fun update(diary: Diary) {
        withContext(Dispatchers.IO) {
            diaryDatabase.update(diary)
        }
    }

    private fun updateDiary() {
        uiScope.launch {
            val newDiary = Diary()
            newDiary.id = id
            newDiary.name = name
            newDiary.date = date
            newDiary.weight = convertStringToFloat(weight, weightUnit.value!!)
            newDiary.length = convertStringToFloat(length, lengthUnit.value!!)
            newDiary.memo = if (memo.isNullOrEmpty()) null else memo
            Log.d("DEBUG", "Update Diary -> $newDiary")
            update(newDiary)
            _navigateToDiaryDetail.value = true
        }
    }

    private suspend fun getDiary(id: Long): Diary {
        return withContext(Dispatchers.IO) {
            diaryDatabase.getDiary(id)
        }
    }

    private suspend fun getNames(): List<String> {
        return withContext(Dispatchers.IO) {
            profileDatabase.getNames()
        }
    }

    private suspend fun getProfile(name: String): Profile {
        return withContext(Dispatchers.IO) {
            profileDatabase.getProfile(name)
        }
    }

    private suspend fun clear(name: String) {
        withContext(Dispatchers.IO) {
            diaryDatabase.clear(name)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}