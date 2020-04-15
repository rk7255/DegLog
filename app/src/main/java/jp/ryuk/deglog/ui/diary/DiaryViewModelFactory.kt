package jp.ryuk.deglog.ui.diary

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.data.DiaryDao

class DiaryViewModelFactory(
    private val diaryDatabase: DiaryDao,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiaryViewModel::class.java)) {
            return DiaryViewModel(diaryDatabase, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}