package jp.ryuk.deglog.ui.diarydetail

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.ui.dashboard.Dashboard
import jp.ryuk.deglog.utilities.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiaryDetailViewModel(
    private val diaryId: Long,
    selectedName: String,
    private val diaryDatabase: DiaryDao,
    profileDatabase: ProfileDao
) : ViewModel() {

    val diaries = diaryDatabase.getDiariesLive(selectedName)
    val profile = profileDatabase.getProfileLive(selectedName)

    var details = MediatorLiveData<List<Detail>>()
    var diaryPosition = MediatorLiveData<Int>()

    var diariesLoaded = MutableLiveData<Boolean>()
    var profileLoaded = MutableLiveData<Boolean>()
    var detailsLoaded = MutableLiveData<Boolean>()

    var position = 0

    var weight = MediatorLiveData<Dashboard>()
    var length = MediatorLiveData<Dashboard>()
    var date = MediatorLiveData<String>()
    var age = MediatorLiveData<String>()
    var weightUnit = MediatorLiveData<String>()
    var lengthUnit = MediatorLiveData<String>()

    fun sectionLoaded() {
        if (diariesLoaded.value == true && profileLoaded.value == true) {
            setDetails()
        }
    }

    private fun setDetails() {
        val detailList = mutableListOf<Detail>()
        diaries.value!!.filter { it.todo == null }.forEach {
            val detail = Detail(
                id = it.id,
                date = it.date,
                name = it.name,
                weight = it.convertWeightUnit(profile.value?.weightUnit ?: "g", true),
                length = it.convertLengthUnit(profile.value?.lengthUnit ?: "mm", true),
                memo = it.memo,
                age = profile.value?.getAge(it.date) ?: ""
            )
            detailList.add(detail)
        }
        details.value = detailList.reversed()

        val ids = details.value!!.map(Detail::id)
        diaryPosition.value = ids.indexOf(diaryId)
        position = diaryPosition.value ?: 0

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

        val weightLatest = if (detailSelected.weight.isNullOrEmpty()) "-" else detailSelected.weight!!
        val weightPrev = detailListBefore.findLast { !it.weight.isNullOrEmpty() }?.weight ?: "-"
        val weightNext = detailListAfter.find { !it.weight.isNullOrEmpty() }?.weight ?: "-"
        val lengthLatest = if (detailSelected.length.isNullOrEmpty()) "-" else detailSelected.length!!
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

        date.value = convertLongToDateStringInTime(detailSelected.date)
        age.value = detailSelected.age

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

    fun onWeightBack() {
        val detailList = details.value!!
        val nowPos = if (detailList.size == diaryPosition.value!!)
            diaryPosition.value!! else diaryPosition.value!! + 1

        val detailListBefore = detailList.subList(0, nowPos - 1)
        val prev = detailListBefore.findLast { !it.weight.isNullOrEmpty() }?.id ?: -1L

        if (prev != -1L) {
            val ids = detailList.map(Detail::id)
            diaryPosition.value = ids.indexOf(prev)
        }
    }

    fun onWeightNext() {
        val detailList = details.value!!
        val nowPos = if (detailList.size == diaryPosition.value!!)
            diaryPosition.value!! else diaryPosition.value!! + 1

        val detailListAfter = detailList.subList(nowPos, detailList.size)
        val prev = detailListAfter.find { !it.weight.isNullOrEmpty() }?.id ?: -1L

        if (prev != -1L) {
            val ids = detailList.map(Detail::id)
            diaryPosition.value = ids.indexOf(prev)
        }
    }

    fun onLengthBack() {
        val detailList = details.value!!
        val nowPos = if (detailList.size == diaryPosition.value!!)
            diaryPosition.value!! else diaryPosition.value!! + 1

        val detailListBefore = detailList.subList(0, nowPos - 1)
        val prev = detailListBefore.findLast { !it.length.isNullOrEmpty() }?.id ?: -1L

        if (prev != -1L) {
            val ids = detailList.map(Detail::id)
            diaryPosition.value = ids.indexOf(prev)
        }
    }

    fun onLengthNext() {
        val detailList = details.value!!
        val nowPos = if (detailList.size == diaryPosition.value!!)
            diaryPosition.value!! else diaryPosition.value!! + 1

        val detailListAfter = detailList.subList(nowPos, detailList.size)
        val prev = detailListAfter.find { !it.length.isNullOrEmpty() }?.id ?: -1L

        if (prev != -1L) {
            val ids = detailList.map(Detail::id)
            diaryPosition.value = ids.indexOf(prev)
        }
    }

    fun editDiary(position: Int): Long = details.value!![position].id

    fun deleteDiary(position: Int) {
        val detail = details.value!![position]
        deleteById(detail.id)
    }

    private suspend fun delete(id: Long) {
        withContext(Dispatchers.IO) { diaryDatabase.deleteById(id) }
    }

    private fun deleteById(id: Long) {
        viewModelScope.launch { delete(id) }
    }

}
