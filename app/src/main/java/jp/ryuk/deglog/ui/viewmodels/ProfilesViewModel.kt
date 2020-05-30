package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.database.ProfileRepository

class ProfilesViewModel internal constructor(
    profileRepository: ProfileRepository
) : ViewModel() {

    val profiles = profileRepository.getAllProfile()

    private val _navigateToEditProfile = MutableLiveData<String?>()
    val navigateToEditProfile: LiveData<String?> get() = _navigateToEditProfile
    fun doneNavigateToNewProfile() { _navigateToEditProfile.value = null }
    fun onClickProfile(name: String) { _navigateToEditProfile.value = name }
}
