package jp.ryuk.deglog.ui.diarydetail

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.ProfileDao
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
                weight = it.convertWeightUnit(profile.value?.weightUnit ?: "g", true),
                length = it.convertLengthUnit(profile.value?.lengthUnit ?: "mm", true),
                memo = it.memo,
                age = profile.value?.getAge(it.date) ?: ""
            )
            detailList.add(detail)
        }
        details.value = detailList.reversed()

        val ids = diaries.value!!.map(Diary::id).reversed()
        diaryPosition.value = ids.indexOf(diaryId)
    }

    /**
     * onClick
     */
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
