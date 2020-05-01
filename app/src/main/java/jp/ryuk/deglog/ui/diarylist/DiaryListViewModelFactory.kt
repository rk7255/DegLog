package jp.ryuk.deglog.ui.diarylist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.ProfileDao

class DiaryListViewModelFactory(
    private val selectedName: String = "",
    private val diaryDatabase: DiaryDao,
    private val profileDatabase: ProfileDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryListViewModel::class.java)) {
            return DiaryListViewModel(selectedName, diaryDatabase, profileDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}