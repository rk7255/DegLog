package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ryuk.deglog.database.Diary
import jp.ryuk.deglog.database.DiaryRepository
import jp.ryuk.deglog.database.Profile
import jp.ryuk.deglog.database.ProfileRepository
import jp.ryuk.deglog.utilities.*
import kotlinx.coroutines.launch
import java.util.*

class NewDiaryViewModel internal constructor(
    private val diaryId: Long,
    selectedName: String,
    private val diaryRepository: DiaryRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val diary = diaryRepository.getDiary(diaryId)
    val nameList = profileRepository.getNames()

    var date = 0L
    val dateString = MutableLiveData<String>()
    val timeString = MutableLiveData<String>()
    val nameString = MutableLiveData<String>()
    val noteString = MutableLiveData<String>()
    val weightString = MutableLiveData<String>()
    val lengthString = MutableLiveData<String>()

    private val isNew = diaryId == -1L
    val submit = MutableLiveData<Int?>()
    val submitError = MutableLiveData<Int?>()

    init {
        if (diaryId == -1L) {
            val now = Calendar.getInstance()
            date = now.timeInMillis
            nameString.value = selectedName
            dateString.value = Converter.longToDateStringJp(now.timeInMillis)
            timeString.value = Converter.longToTimeStringJp(now.timeInMillis)
        }
    }

    fun setDiary(diary: Diary) {
        date = diary.date
        dateString.value = Converter.longToDateStringJp(diary.date)
        timeString.value = Converter.longToTimeStringJp(diary.date)
        nameString.value = diary.name
        weightString.value = diary.weight?.toString()
        lengthString.value = diary.length?.toString()
        noteString.value = diary.note
    }

    fun submit() {
        val msg = isValid()

        if (msg == MessageCode.NAME_EMPTY) {
            submitError.value = msg
        } else {
            if (weightString.value.isNullOrEmpty() &&
                lengthString.value.isNullOrEmpty() &&
                noteString.value.isNullOrEmpty()) {
                submitError.value = MessageCode.NUMBER_EMPTY
            } else {
                if (msg == MessageCode.NAME_UNREGISTERED) profileRegister()
                diaryRegister()
                submit.value = msg
            }
        }
    }

    private fun diaryRegister() {
        val newDiary = Diary(
            name = nameString.value!!,
            date = date,
            weight = stringToFloat(weightString.value),
            length = stringToFloat(lengthString.value),
            note = noteString.value
        )
        if (!isNew) newDiary.id = diaryId

        insertDiary(newDiary)
    }

    private fun stringToFloat(string: String?): Float? {
        return when {
            string.isNullOrEmpty() -> null
            string == "." -> 0f
            else -> string.toFloat()
        }
    }

    private fun profileRegister() {
        val newProfile = Profile(
            name = nameString.value!!
        )
        insertProfile(newProfile)
    }

    private fun isValid(): Int {
        val name = nameString.value ?: ""
        val nameList = nameList.value ?: listOf()

        return when {
            name.isEmpty() -> MessageCode.NAME_EMPTY
            !nameList.contains(name) -> MessageCode.NAME_UNREGISTERED
            !isNew -> MessageCode.EDIT
            else -> MessageCode.COLLECT
        }
    }

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        val old = date
        calendar.set(year, month, day, old.getHour(), old.getMinute(), 0)
        date = calendar.timeInMillis
        dateString.value = Converter.longToDateStringJp(calendar.timeInMillis)
    }

    fun setTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance()
        val old = date
        calendar.set(old.getYear(), old.getMonth(), old.getDayOfMonth(), hour, minute, 0)
        date = calendar.timeInMillis
        timeString.value = Converter.longToTimeStringJp(calendar.timeInMillis)
    }

    private fun insertDiary(diary: Diary) {
        viewModelScope.launch { diaryRepository.insert(diary) }
    }

    private fun insertProfile(profile: Profile) {
        viewModelScope.launch { profileRepository.insert(profile) }
    }
}