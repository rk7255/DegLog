package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.database.*

class DiaryDetailViewModelFactory(
    private val diaryId: Long,
    private val selectedName: String,
    private val diaryRepository: DiaryRepository,
    private val profileRepository: ProfileRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DiaryDetailViewModel(
            diaryId,
            selectedName,
            diaryRepository,
            profileRepository
        ) as T
    }
}