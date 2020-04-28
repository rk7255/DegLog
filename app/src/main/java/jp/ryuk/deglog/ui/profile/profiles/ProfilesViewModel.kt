package jp.ryuk.deglog.ui.profile.profiles

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.tag
import kotlinx.coroutines.*

class ProfilesViewModel(
    private val profileDatabase: ProfileDao
) : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private var _profiles = listOf<Profile>()
    var profiles = MediatorLiveData<List<Profile>>()

    init {
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}