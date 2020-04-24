package jp.ryuk.deglog.ui.diarylist.lists.memo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.data.DiaryDao

class MemoViewModelFactory(
    private val selectedName: String = "",
    private val diaryDatabase: DiaryDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MemoViewModel::class.java)) {
            return MemoViewModel(
                selectedName,
                diaryDatabase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}