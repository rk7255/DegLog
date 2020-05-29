package jp.ryuk.deglog.ui.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.R
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

    fun applyFilter(context: Context, checked: List<String>) {
        var newDiaries = allDiary.value!!
        checked.forEach { c ->
            when (c) {
                context.getString(R.string.weight) -> newDiaries = newDiaries.filter { it.weight != null }
                context.getString(R.string.length) -> newDiaries = newDiaries.filter { it.length != null }
                context.getString(R.string.note) -> newDiaries = newDiaries.filter { it.note != null }
            }
        }
        diaries.value = newDiaries
    }
}