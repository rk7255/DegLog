package jp.ryuk.deglog.ui.profile.newprofile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.tag
import kotlinx.coroutines.*

class NewProfileViewModel(
    private val profileDatabase: ProfileDao
) : ViewModel() {
    private var viewModelJob = Job()
    private var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var name = ""
    var gender = "不明"
    var type = ""
    var birthday: Long? = null
    var birthdayString = ""
    var unitWeight = "g"
    var unitLength = "mm"

    /**
     * onClick
     */
    fun onSubmit() {
        if (isInputDataValid()) insertNewProfile()
    }

    private fun isInputDataValid(): Boolean {
        val isValid = name.isNotEmpty()
        _submitError.value = !isValid
        return isValid
    }

    fun onCancel() {
        _backToProfiles.value = true
    }

    /**
     * LiveData
     */
    private var _navigateToProfiles = MutableLiveData<Boolean>()
    val navigateToProfiles: LiveData<Boolean> get() = _navigateToProfiles
    fun doneNavigateToProfiles() {
        _navigateToProfiles.value = false
    }

    private var _backToProfiles = MutableLiveData<Boolean>()
    val backToProfiles: LiveData<Boolean> get() = _backToProfiles
    fun doneBackToProfiles() {
        _backToProfiles.value = false
    }

    private var _submitError = MutableLiveData<Boolean>()
    val submitError: LiveData<Boolean> get() = _submitError

    /**
     * Database
     */
    private suspend fun insert(profile: Profile) {
        withContext(Dispatchers.IO) {
            profileDatabase.insert(profile)
        }
    }

    private fun insertNewProfile() {
        uiScope.launch {
            val newProfile = Profile()
            newProfile.name = name
            newProfile.gender = gender
            newProfile.type = type
            newProfile.birthday = birthday
            newProfile.weightUnit = unitWeight
            newProfile.lengthUnit = unitLength
            Log.d(tag, "Insert Profile -> $newProfile")
            insert(newProfile)
            _navigateToProfiles.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}