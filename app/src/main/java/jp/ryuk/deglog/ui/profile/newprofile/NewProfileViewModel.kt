package jp.ryuk.deglog.ui.profile.newprofile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.convertLongToDateString
import jp.ryuk.deglog.utilities.tag
import kotlinx.coroutines.*

class NewProfileViewModel(
    private val selectedName: String,
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

    var isInsert = true


    init {
        if (selectedName.isNotEmpty()) {
            initialize()
            isInsert = false
        }
    }

    // 更新する時のみ初期化処理
    private fun initialize() {
        uiScope.launch {
            val profile = get(selectedName)
            name = profile.name
            gender = profile.gender
            type = profile.type
            birthday = profile.birthday
            birthday?.let { birthdayString = convertLongToDateString(it) }
            unitWeight = profile.weightUnit
            unitLength = profile.lengthUnit
            _initialized.value = true

        }
    }

    /**
     * onClick
     */
    fun onSubmit() {
        if (isInsert) {
            if (isInputDataValid()) insertNewProfile()
        } else {
            if (isInputDataValid()) updateProfile()
        }
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
    private var _initialized = MutableLiveData<Boolean>()
    val initialized: LiveData<Boolean> get() = _initialized
    fun doneInitialize() {
        _initialized.value = false
    }

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

    private suspend fun update(profile: Profile) {
        withContext(Dispatchers.IO) {
            profileDatabase.update(profile)
        }
    }

    private fun updateProfile() {
        uiScope.launch {
            val newProfile = Profile()
            newProfile.name = name
            newProfile.gender = gender
            newProfile.type = type
            newProfile.birthday = birthday
            newProfile.weightUnit = unitWeight
            newProfile.lengthUnit = unitLength
            Log.d(tag, "Update Profile -> $newProfile")
            update(newProfile)
            _navigateToProfiles.value = true
        }
    }

    private suspend fun get(key: String): Profile {
        return withContext(Dispatchers.IO) {
            profileDatabase.getProfile(key)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}