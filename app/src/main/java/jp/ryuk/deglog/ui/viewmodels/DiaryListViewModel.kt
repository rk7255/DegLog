package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.database.Diary
import jp.ryuk.deglog.database.DiaryRepository
import jp.ryuk.deglog.database.ProfileRepository

class DiaryListViewModel internal constructor(
    selectedName: String,
    diaryRepository: DiaryRepository,
    profileRepository: ProfileRepository
) : ViewModel() {

    val allDiary = diaryRepository.getDiaries(selectedName)
    val profile = profileRepository.getProfile(selectedName)

    val diaries = MutableLiveData<List<Diary>>()

    fun applyFilter(checked: List<String>) {
        var newDiaries = allDiary.value!!
        checked.forEach { c ->
            when (c) {
                "体重" -> newDiaries = newDiaries.filter { it.weight != null }
                "体長" -> newDiaries = newDiaries.filter { it.length != null }
                "メモ" -> newDiaries = newDiaries.filter { it.note != null }
            }
        }
        diaries.value = newDiaries
    }
}