package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ryuk.deglog.database.DiaryRepository
import jp.ryuk.deglog.database.ProfileRepository
import jp.ryuk.deglog.ui.data.Dashboard
import jp.ryuk.deglog.ui.data.Detail
import jp.ryuk.deglog.utilities.Converter
import jp.ryuk.deglog.utilities.getAge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryDetailViewModel internal constructor(
    private val diaryId: Long,
    selectedName: String,
    private val diaryRepository: DiaryRepository,
    profileRepository: ProfileRepository
) : ViewModel() {

    val diaries = diaryRepository.getDiaries(selectedName)
    val profile = profileRepository.getProfile(selectedName)

    var details = MediatorLiveData<List<Detail>>()
    var diaryPosition = MediatorLiveData<Int>()

    var diariesLoaded = MutableLiveData<Boolean>()
    var profileLoaded = MutableLiveData<Boolean>()
    var detailsLoaded = MutableLiveData<Boolean>()

    var position = -1

    var weight = MediatorLiveData<Dashboard>()
    var length = MediatorLiveData<Dashboard>()
    var date = MediatorLiveData<String>()
    var age = MediatorLiveData<String>()
    var memo = MediatorLiveData<String>()
    var weightUnit = MediatorLiveData<String>()
    var lengthUnit = MediatorLiveData<String>()

    fun sectionLoaded() {
        if (diariesLoaded.value == true && profileLoaded.value == true) {
            setDetails()
        }
    }

    private fun setDetails() {
        val detailList = mutableListOf<Detail>()
        diaries.value!!.forEach {
            val detail = Detail(
                id = it.id,
                date = it.date,
                name = it.name,
//                weight = it.convertWeightUnit(profile.value?.weightUnit ?: "g", true),
//                length = it.convertLengthUnit(profile.value?.lengthUnit ?: "mm", true),
//                memo = it.memo,
                age = profile.value?.getAge(it.date) ?: ""
            )
            detailList.add(detail)
        }
        details.value = detailList.reversed()

        val ids = details.value!!.map(Detail::id)
        diaryPosition.value = if (position != -1) position else ids.indexOf(diaryId)

        weightUnit.value = profile.value?.weightUnit ?: "g"
        lengthUnit.value = profile.value?.lengthUnit ?: "mm"

        detailsLoaded.value = true
    }

    fun getDetail() {
        val detailList = details.value!!
        val pos = if (detailList.size == diaryPosition.value!!)
            diaryPosition.value!! else diaryPosition.value!! + 1

        val detailSelected = detailList[pos - 1]
        val detailListBefore = detailList.subList(0, pos - 1)
        val detailListAfter = detailList.subList(pos, detailList.size)

        val weightLatest =
            if (detailSelected.weight.isNullOrEmpty()) "-" else detailSelected.weight!!
        val weightPrev = detailListBefore.findLast { !it.weight.isNullOrEmpty() }?.weight ?: "-"
        val weightNext = detailListAfter.find { !it.weight.isNullOrEmpty() }?.weight ?: "-"
        val lengthLatest =
            if (detailSelected.length.isNullOrEmpty()) "-" else detailSelected.length!!
        val lengthPrev = detailListBefore.findLast { !it.length.isNullOrEmpty() }?.length ?: "-"
        val lengthNext = detailListAfter.find { !it.length.isNullOrEmpty() }?.length ?: "-"

        weight.value = Dashboard(
            latest = weightLatest,
            prev = weightPrev,
            recent = weightNext
        )

        length.value = Dashboard(
            latest = lengthLatest,
            prev = lengthPrev,
            recent = lengthNext
        )

        date.value = Converter.longToDateAndTimeString(detailSelected.date)
        age.value = detailSelected.age
        memo.value = detailSelected.memo
    }


    fun onDateBack() {
        val nowPos = diaryPosition.value ?: position
        if (nowPos > 0) diaryPosition.value = nowPos - 1
    }

    fun onDateNext() {
        val nowPos = diaryPosition.value ?: position
        val maxPos = details.value?.size?.minus(1) ?: 0
        if (nowPos < maxPos) diaryPosition.value = nowPos + 1
    }

    private fun movePage(direction: String, witch: String) {
        val detailList = details.value!!
        val nowPos = if (detailList.size == diaryPosition.value!!)
            diaryPosition.value!! else diaryPosition.value!! + 1

        val splitList = when (direction) {
            "next" -> detailList.subList(nowPos, detailList.size)
            "back" -> detailList.subList(0, nowPos - 1)
            else -> listOf()
        }

        val result = when (witch) {
            "w" -> {
                when (direction) {
                    "next" -> splitList.find { !it.weight.isNullOrEmpty() }?.id ?: -1L
                    "back" -> splitList.findLast { !it.weight.isNullOrEmpty() }?.id ?: -1L
                    else -> -1L
                }
            }
            "l" -> {
                when (direction) {
                    "next" -> splitList.find { !it.length.isNullOrEmpty() }?.id ?: -1L
                    "back" -> splitList.findLast { !it.length.isNullOrEmpty() }?.id ?: -1L
                    else -> -1L
                }
            }
            else -> -1L
        }

        if (result != -1L) {
            val ids = detailList.map(Detail::id)
            diaryPosition.value = ids.indexOf(result)
        }
    }

    fun onWeightBack() {
        movePage("back", "w")
    }

    fun onWeightNext() {
        movePage("next", "w")
    }

    fun onLengthBack() {
        movePage("back", "l")
    }

    fun onLengthNext() {
        movePage("next", "l")
    }


    fun editDiary(position: Int): Long = details.value!![position].id

    fun deleteDiary(position: Int) {
        val detail = details.value!![position]
        deleteById(detail.id)
    }

    private fun deleteById(id: Long) {
        viewModelScope.launch { diaryRepository.deleteById(id) }
    }

}
