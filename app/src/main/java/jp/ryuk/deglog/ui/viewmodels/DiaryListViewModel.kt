package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.database.*

class DiaryListViewModel(
    selectedName: String,
    diaryRepository: DiaryRepository,
    profileRepository: ProfileRepository
) : ViewModel() {

    val diaries = diaryRepository.getDiaries(selectedName)
    val profile = profileRepository.getProfile(selectedName)

    private var allLoaded = MutableLiveData<Boolean>()
    var diariesLoaded = MutableLiveData<Boolean>()
    var profileLoaded = MutableLiveData<Boolean>()

    var checkedWeight = MediatorLiveData<Boolean>()
    var checkedLength = MediatorLiveData<Boolean>()
    var checkedMemo = MediatorLiveData<Boolean>()

    val diaryList = MutableLiveData<List<Diary>>()
    var flg = MutableLiveData<Boolean>()

    fun sectionLoaded() {
        if (diariesLoaded.value == true && profileLoaded.value == true) {
            allLoaded.value = true
            applyFilter()
        }
    }

    fun applyFilter() {
        var newDiaries = diaries.value
        newDiaries = newDiaries!!.filter { it.todo == null }
        if (checkedWeight.value == true) newDiaries = newDiaries.filter { it.weight != null }
        if (checkedLength.value == true) newDiaries = newDiaries.filter { it.length != null }
        if (checkedMemo.value == true) newDiaries = newDiaries.filter { it.memo != null }
        diaryList.value = newDiaries
    }

//    private fun convertDiaryToDetailList(diaries: List<Diary>): List<Diary> {
//        val weightUnit = profile.value?.weightUnit ?: "g"
//        val lengthUnit = profile.value?.lengthUnit ?: "mm"
//
//        val list = mutableListOf<Diary>()
//        diaries.forEach {
//            val data = Diary(
//                id = it.id,
//                date = convertLongToDateStringOutYear(it.date),
//                weight = it.convertWeightUnit(weightUnit, true),
//                length = it.convertLengthUnit(lengthUnit, true),
//                hasComment = it.memo.isNullOrEmpty()
//            )
//            list.add(data)
//        }
//
//        return list
//    }
}