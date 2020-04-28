package jp.ryuk.deglog.ui.profile.profiles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.ryuk.deglog.data.ProfileDao

class ProfilesViewModelFactory(
    private val profileDatabase: ProfileDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfilesViewModel::class.java)) {
            return ProfilesViewModel(profileDatabase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}