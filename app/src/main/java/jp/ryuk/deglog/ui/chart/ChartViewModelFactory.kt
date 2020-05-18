package jp.ryuk.deglog.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.ProfileDao

class ChartViewModelFactory(
    private val diaryDatabase: DiaryDao,
    private val profileDao: ProfileDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChartViewModel::class.java)) {
            return ChartViewModel(diaryDatabase, profileDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}