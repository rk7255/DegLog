package jp.ryuk.deglog.ui.diarydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.data.DiaryDao

class DiaryDetailViewModelFactory (
    private val diaryKey: Long,
    private val diaryDatabase: DiaryDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryDetailViewModel::class.java)) {
            return DiaryDetailViewModel(diaryKey, diaryDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}