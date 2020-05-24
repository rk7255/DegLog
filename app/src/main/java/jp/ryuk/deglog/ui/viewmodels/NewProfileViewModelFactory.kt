package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.database.DiaryRepository
import jp.ryuk.deglog.database.ProfileRepository

class NewProfileViewModelFactory(
    private val selectedName: String,
    private val diaryRepository: DiaryRepository,
    private val profileRepository: ProfileRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewProfileViewModel::class.java)) {
            return NewProfileViewModel(
                selectedName,
                diaryRepository,
                profileRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}