package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.*
import jp.ryuk.deglog.database.*
import jp.ryuk.deglog.utilities.*
import kotlinx.coroutines.launch
import java.util.*

class NewProfileViewModel internal constructor(
    private val selectedName: String,
    diaryRepository: DiaryRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val profile = profileRepository.getProfile(selectedName)
    val nameList = profileRepository.getNames()

    private var birthday: Long? = null
    val birthdayString = MutableLiveData<String>()
    val nameString = MutableLiveData<String>()
    val typeString = MutableLiveData<String>()
    private val genderString = MutableLiveData<String>()
    var uriString: String? = null

    private val isNew = selectedName.isEmpty()
    val submit = MutableLiveData<Int?>()
    val submitError = MutableLiveData<Int?>()

    val selectedColor = MediatorLiveData<Int?>()
    fun onSelectColor(select: Int) { selectedColor.value = select }


    fun submit() {
        val error = isValid()
        if (error != null) {
            submitError.value = error
            return
        }

        val newProfile = Profile(
            name = nameString.value!!,
            type = typeString.value,
            gender = genderString.value,
            birthday = birthday,
            color = selectedColor.value,
            uri = uriString
        )

        val msg = when {
            !isNew && selectedName != nameString.value -> {
                updateProfile(newProfile)
                MessageCode.EDIT
            }
            else -> {
                insertProfile(newProfile)
                MessageCode.COLLECT
            }
        }
        submit.value = msg
    }

    private fun isValid(): Int? {
        val name = nameString.value ?: ""
        val nameList = nameList.value ?: listOf()
        val registered = nameList.contains(name)

        return when {
            name.isEmpty() -> MessageCode.NAME_EMPTY
            isNew && registered -> MessageCode.NAME_REGISTERED
            !isNew && registered && selectedName != name -> MessageCode.NAME_REGISTERED
            else -> null
        }
    }

    private fun updateProfile(profile: Profile) {
        insert(profile)
        delete(selectedName)
    }

    private fun insertProfile(profile: Profile) {
        insert(profile)
    }


    fun setProfile(profile: Profile) {
        nameString.value = profile.name
        birthday = profile.birthday
        birthday?.let { birthdayString.value = Converter.longToDateStringJp(it) }
        typeString.value = profile.type
        uriString = profile.uri
        genderString.value = profile.gender
    }

    fun setGender(gender: String) {
        genderString.value = gender
    }

    fun setDate(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day, 0, 0, 0)
        birthday = calendar.timeInMillis
        birthdayString.value = Converter.longToDateStringJp(calendar.timeInMillis)
    }

    private fun insert(profile: Profile) {
        viewModelScope.launch { profileRepository.insert(profile) }
    }

    private fun delete(name: String) {
        viewModelScope.launch { profileRepository.deleteByName(name) }
    }


}
//
//
//    val profile = profileRepository.getProfile(selectedName)
//
//    private var isNew = true
//    var isNameChanged = false
//
//    var name = MediatorLiveData<String>()
//    var gender = MediatorLiveData<String>()
//    var type = MediatorLiveData<String>()
//    var birthday: Long? = null
//    var birthdayString = MediatorLiveData<String>()
//    var weightUnit = MediatorLiveData<String>()
//    var lengthUnit = MediatorLiveData<String>()
//
//    private var _submit = MutableLiveData<Boolean>()
//    val submit: LiveData<Boolean> get() = _submit
//
//    private var _submitError = MutableLiveData<Boolean>()
//    val submitError: LiveData<Boolean> get() = _submitError
//
//    private var _onDateClick = MutableLiveData<Boolean>()
//    val onDateCLick: LiveData<Boolean> get() = _onDateClick
//    fun doneOnDateClick(time: Long) {
//        birthday = time
//        birthdayString.value = Converter.longToDateString(birthday!!)
//        _onDateClick.value = false
//    }
//
//    val selectedColor = MediatorLiveData<Int?>()
//    fun onSelectColor(select: Int) { selectedColor.value = select }
//
//    private var _confirmUpdate = MutableLiveData<Profile?>()
//    val confirmUpdate: LiveData<Profile?> get() = _confirmUpdate
//
//    init {
//        name.value = selectedName
//        weightUnit.value = "g"
//        lengthUnit.value = "mm"
//    }
//
//    fun setValues() {
//        isNew = false
//        birthday = profile.value?.birthday
//        birthday?.let { birthdayString.value = Converter.longToDateAndTimeString(it) }
//        gender.value = profile.value?.gender ?: "不明"
//        type.value = profile.value?.type
//        selectedColor.value = profile.value!!.color
//    }
//
//    fun onSubmit() {
//        if (isValid()) {
//            val profile = Profile(
//                name = name.value!!,
//                type = type.value,
//                gender = gender.value ?: "不明",
//                birthday = birthday,
//                color = selectedColor.value
//            )
//            if (isNew) {
//                insertProfile(profile)
//                _submit.value = true
//            } else {
//                if (name.value == selectedName) {
//                    updateProfile(profile)
//                    _submit.value = true
//                } else {
//                    _confirmUpdate.value = profile
//                }
//            }
//        } else {
//            _submitError.value = true
//        }
//    }
//
//    fun updateAndChange(profile: Profile) {
//        insertProfile(profile)
//        changeProfile(selectedName, profile.name)
//        isNameChanged = true
//        _submit.value = true
//    }
//
//    private fun isValid(): Boolean = !name.value.isNullOrEmpty()
//
//    fun onCancel() {
//        _submit.value = true
//    }
//
//    fun onBirthday() {
//        _onDateClick.value = true
//    }
//
//    private suspend fun insert(profile: Profile) {
//        withContext(Dispatchers.IO) {
//            profileRepository.insert(profile)
//        }
//    }
//
//    private fun insertProfile(profile: Profile) {
//        viewModelScope.launch { insert(profile) }
//    }
//
//    private suspend fun update(profile: Profile) {
//        withContext(Dispatchers.IO) {
//            profileRepository.insert(profile)
//        }
//    }
//
//    private fun updateProfile(profile: Profile) {
//        viewModelScope.launch { update(profile) }
//    }
//
//    private fun changeProfile(oldName: String, newName: String) {
//        viewModelScope.launch {
//            diaryRepository.changeName(oldName, newName)
//            profileRepository.deleteByName(oldName)
//        }
//    }
//}