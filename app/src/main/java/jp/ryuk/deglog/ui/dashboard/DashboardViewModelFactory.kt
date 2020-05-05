package jp.ryuk.deglog.ui.dashboard

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.ProfileDao

class DashboardViewModelFactory(
    private val diaryDatabase: DiaryDao,
    private val profileDao: ProfileDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(diaryDatabase, profileDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}