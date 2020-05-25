package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ryuk.deglog.database.Diary
import jp.ryuk.deglog.database.DiaryRepository
import jp.ryuk.deglog.database.ProfileRepository
import jp.ryuk.deglog.ui.data.DisplayData
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

class DiaryDetailViewModel internal constructor(
    diaryId: Long,
    selectedName: String,
    private val diaryRepository: DiaryRepository,
    profileRepository: ProfileRepository
) : ViewModel() {

    val allDiary = diaryRepository.getDiaries(selectedName)
    val profile = profileRepository.getProfile(selectedName)

    var id = diaryId
    var pos = 0

    val weightData = MutableLiveData<DisplayData>()
    val lengthData = MutableLiveData<DisplayData>()
    val date = MutableLiveData<Long>()
    val hasNote = MutableLiveData<Boolean>()
    val note = MutableLiveData<String>()

    private val _animTrigger = MutableLiveData<String?>()
    val animTrigger: LiveData<String?> get() = _animTrigger

    fun initDiary() {
        val diaryList = allDiary.value!!
        pos = diaryList.map(Diary::id).indexOf(id)
        getDiary()
    }

    fun setDiary(which: String, moveTo: String) {
        val diaryList = allDiary.value!!
        val before = diaryList.subList(0, pos)
        val after = diaryList.subList(min(pos + 1, diaryList.size), diaryList.size)

        when (which) {
            "d" -> {
                when (moveTo) {
                    "next" -> pos = max(pos - 1, 0)
                    "back" -> pos = min(pos + 1, diaryList.size - 1)
                }
            }
            "w" -> {
                when (moveTo) {
                    "next" -> {
                        val next = before.lastOrNull { it.weight != null } ?: return
                        pos = diaryList.map(Diary::id).indexOf(next.id)
                    }
                    "back" -> {
                        val prev = after.firstOrNull { it.weight != null } ?: return
                        pos = diaryList.map(Diary::id).indexOf(prev.id)
                    }
                }
            }
            "l" -> {
                when (moveTo) {
                    "next" -> {
                        val next = before.lastOrNull { it.length != null } ?: return
                        pos = diaryList.map(Diary::id).indexOf(next.id)
                    }
                    "back" -> {
                        val prev = after.firstOrNull { it.length != null } ?: return
                        pos = diaryList.map(Diary::id).indexOf(prev.id)
                    }
                }
            }
        }

        getDiary()
        _animTrigger.value = moveTo
    }

    private fun getDiary() {
        val diaryList = allDiary.value!!

        val now = diaryList[pos]
        val before = diaryList.subList(0, pos)
        val after = diaryList.subList(min(pos + 1, diaryList.size), diaryList.size)

        val wLatest = now.weight
        val wPrev = after.mapNotNull(Diary::weight).firstOrNull()
        val wNext = before.mapNotNull(Diary::weight).lastOrNull()

        val lLatest = now.length
        val lPrev = after.mapNotNull(Diary::length).firstOrNull()
        val lNext = before.mapNotNull(Diary::length).lastOrNull()

        weightData.value = DisplayData(
            latest = if (wLatest == null) "-" else "${wLatest.toInt()} g",
            prev = if (wPrev == null) "-" else "${wPrev.toInt()} g",
            recent = if (wNext == null) "-" else "${wNext.toInt()} g"
        )

        lengthData.value = DisplayData(
            latest = if (lLatest == null) "-" else "${lLatest.toInt()} mm",
            prev = if (lPrev == null) "-" else "${lPrev.toInt()} mm",
            recent = if (lNext == null) "-" else "${lNext.toInt()} mm"
        )

        id = now.id
        date.value = now.date
        note.value = now.note
        hasNote.value = !now.note.isNullOrEmpty()
    }

    fun deleteDiary() {
        viewModelScope.launch { diaryRepository.deleteById(id) }
    }
}