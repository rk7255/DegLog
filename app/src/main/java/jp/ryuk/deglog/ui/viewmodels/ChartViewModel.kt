package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.database.Diary
import jp.ryuk.deglog.database.DiaryRepository
import jp.ryuk.deglog.database.Profile
import jp.ryuk.deglog.database.ProfileRepository

class ChartViewModel internal constructor(
    diaryRepository: DiaryRepository,
    profileRepository: ProfileRepository
) : ViewModel() {

    val allDiary: LiveData<List<Diary>> = diaryRepository.getAllDiary()
    val allProfile: LiveData<List<Profile>> = profileRepository.getAllProfile()

    val nameList: List<String>
        get() = allDiary.value?.map(Diary::name)?.distinct() ?: listOf()

}