package jp.ryuk.deglog.ui.profile.newprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.ProfileDao

class NewProfileViewModelFactory(
    private val selectedName: String,
    private val diaryDatabase: DiaryDao,
    private val profileDatabase: ProfileDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewProfileViewModel::class.java)) {
            return NewProfileViewModel(selectedName, diaryDatabase, profileDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}