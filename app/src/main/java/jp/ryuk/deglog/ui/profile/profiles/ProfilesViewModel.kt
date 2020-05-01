package jp.ryuk.deglog.ui.profile.profiles

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import kotlinx.coroutines.*

class ProfilesViewModel(
    private val profileDatabase: ProfileDao
) : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _profiles = listOf<Profile>()
    var profiles = MediatorLiveData<List<Profile>>()

    init {
        initialize()
    }

    private fun initialize() {
        uiScope.launch {
            _profiles = getProfiles()
            profiles.value = _profiles
        }
    }

    /**
     * onClick
     */
    fun onClickProfile(name: String) {
        _navigateToNewProfile.value = name
    }

    fun onClear() {
        uiScope.launch {
            clear()
            initialize()
        }
    }

    /**
     * LiveData
     */
    private var _navigateToNewProfile = MutableLiveData<String?>()
    val navigateToNewProfile: LiveData<String?>
        get() = _navigateToNewProfile
    fun doneNavigateTpNewProfile() {
        _navigateToNewProfile.value = null
    }

    /**
     * Database
     */
    private suspend fun getProfiles(): List<Profile> {
        return withContext(Dispatchers.IO) {
            profileDatabase.getProfiles()
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            profileDatabase.clear()
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}