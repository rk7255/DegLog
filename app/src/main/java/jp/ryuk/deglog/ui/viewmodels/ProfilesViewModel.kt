package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.database.ProfileRepository

class ProfilesViewModel(
    profileRepository: ProfileRepository
) : ViewModel() {

    var profiles = profileRepository.getAllProfile()

    fun onClickProfile(name: String) {
        _navigateToNewProfile.value = name
    }

    private var _navigateToNewProfile = MutableLiveData<String?>()
    val navigateToNewProfile: LiveData<String?> get() = _navigateToNewProfile
    fun doneNavigateToNewProfile() {
        _navigateToNewProfile.value = null
    }
}