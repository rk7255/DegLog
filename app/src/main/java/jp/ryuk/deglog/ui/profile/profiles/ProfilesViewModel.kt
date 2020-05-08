package jp.ryuk.deglog.ui.profile.profiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.ProfileDao

class ProfilesViewModel(
    profileDatabase: ProfileDao
) : ViewModel() {

    var profiles = profileDatabase.getProfiles()

    fun onClickProfile(name: String) {
        _navigateToNewProfile.value = name
    }

    private var _navigateToNewProfile = MutableLiveData<String?>()
    val navigateToNewProfile: LiveData<String?> get() = _navigateToNewProfile
    fun doneNavigateToNewProfile() {
        _navigateToNewProfile.value = null
    }
}