package jp.ryuk.deglog.ui.diarylist.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.data.DiaryDao

class WeightViewModelFactory(
    private val selectedName: String = "",
    private val diaryDatabase: DiaryDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeightViewModel::class.java)) {
            return WeightViewModel(selectedName, diaryDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}