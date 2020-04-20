package jp.ryuk.deglog.ui.newdiary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.ProfileDao

class NewDiaryViewModelFactory(
    private val selectedName: String,
    private val dataSourceDiary: DiaryDao,
    private val dataSourceProfile: ProfileDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewDiaryViewModel::class.java)) {
            return NewDiaryViewModel(selectedName, dataSourceDiary, dataSourceProfile) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}