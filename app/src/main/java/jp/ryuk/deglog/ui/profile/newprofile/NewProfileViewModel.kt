package jp.ryuk.deglog.ui.profile.newprofile

import androidx.lifecycle.*
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.Profile
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.convertLongToDateString
import jp.ryuk.deglog.utilities.convertLongToDateStringInTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NewProfileViewModel(
    private val selectedName: String,
    private val diaryDatabase: DiaryDao,
    private val profileDatabase: ProfileDao
) : ViewModel() {

    val profile = profileDatabase.getProfile(selectedName)

    private var isNew = true
    var isNameChanged = false

    var name = MediatorLiveData<String>()
    var gender = MediatorLiveData<String>()
    var type = MediatorLiveData<String>()
    var birthday: Long? = null
    var birthdayString = MediatorLiveData<String>()
    var weightUnit = MediatorLiveData<String>()
    var lengthUnit = MediatorLiveData<String>()

    private var _submit = MutableLiveData<Boolean>()
    val submit: LiveData<Boolean> get() = _submit

    private var _submitError = MutableLiveData<Boolean>()
    val submitError: LiveData<Boolean> get() = _submitError

    private var _onDateClick = MutableLiveData<Boolean>()
    val onDateCLick: LiveData<Boolean> get() = _onDateClick
    fun doneOnDateClick(time: Long) {
        birthday = time
        birthdayString.value = convertLongToDateString(birthday!!)
        _onDateClick.value = false
    }

    val selectedColor = MediatorLiveData<Int?>()
    fun onSelectColor(select: Int) { selectedColor.value = select }

    private var _confirmUpdate = MutableLiveData<Profile?>()
    val confirmUpdate: LiveData<Profile?> get() = _confirmUpdate

    init {
        name.value = selectedName
        weightUnit.value = "g"
        lengthUnit.value = "mm"
    }

    fun setValues() {
        isNew = false
        birthday = profile.value?.birthday
        birthday?.let { birthdayString.value = convertLongToDateStringInTime(it) }
        gender.value = profile.value?.gender ?: "不明"
        type.value = profile.value?.type
        weightUnit.value = profile.value!!.weightUnit
        lengthUnit.value = profile.value!!.lengthUnit
        selectedColor.value = profile.value!!.color
    }

    fun onSubmit() {
        if (isValid()) {
            val profile = Profile(
                name = name.value!!,
                type = type.value,
                gender = gender.value ?: "不明",
                birthday = birthday,
                weightUnit = weightUnit.value ?: "g",
                lengthUnit = lengthUnit.value ?: "mm",
                color = selectedColor.value
            )
            if (isNew) {
                insertProfile(profile)
                _submit.value = true
            } else {
                if (name.value == selectedName) {
                    updateProfile(profile)
                    _submit.value = true
                } else {
                    _confirmUpdate.value = profile
                }
            }
        } else {
            _submitError.value = true
        }
    }

    fun updateAndChange(profile: Profile) {
        insertProfile(profile)
        changeProfile(selectedName, profile.name)
        isNameChanged = true
        _submit.value = true
    }

    private fun isValid(): Boolean = !name.value.isNullOrEmpty()

    fun onCancel() {
        _submit.value = true
    }

    fun onBirthday() {
        _onDateClick.value = true
    }

    private suspend fun insert(profile: Profile) {
        withContext(Dispatchers.IO) {
            profileDatabase.insert(profile)
        }
    }

    private fun insertProfile(profile: Profile) {
        viewModelScope.launch { insert(profile) }
    }

    private suspend fun update(profile: Profile) {
        withContext(Dispatchers.IO) {
            profileDatabase.update(profile)
        }
    }

    private fun updateProfile(profile: Profile) {
        viewModelScope.launch { update(profile) }
    }

    private suspend fun change(oldName: String, newName: String) {
        withContext(Dispatchers.IO) {
            diaryDatabase.changeName(oldName, newName)
            profileDatabase.deleteByName(oldName)
        }
    }

    private fun changeProfile(oldName: String, newName: String) {
        viewModelScope.launch {
            change(oldName, newName)
        }
    }
}