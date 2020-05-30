package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jp.ryuk.deglog.database.DiaryRepository
import jp.ryuk.deglog.database.Profile
import jp.ryuk.deglog.database.ProfileRepository
import jp.ryuk.deglog.utilities.Converter
import jp.ryuk.deglog.utilities.MessageCode
import kotlinx.coroutines.launch
import java.util.*

class NewProfileViewModel internal constructor(
    private val selectedName: String,
    private val diaryRepository: DiaryRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    val profile = profileRepository.getProfile(selectedName)
    val nameList = profileRepository.getNames()

    private var birthday: Long? = null
    val birthdayString = MutableLiveData<String>()
    val nameString = MutableLiveData<String>()
    val typeString = MutableLiveData<String>()
    private val genderString = MutableLiveData<String>()
    private var iconJsonString: String? = null

    private val isNew = selectedName.isEmpty()
    val submit = MutableLiveData<Boolean>()
    val submitError = MutableLiveData<Int?>()
    val submitMassage = MutableLiveData<Profile>()

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
            icon = iconJsonString
        )

        if (!isNew && selectedName != nameString.value) {
            submitMassage.value = newProfile
            return
        }

        insertProfile(newProfile)
        submit.value = true
    }

    fun changeName(profile: Profile) {
        updateProfile(profile)
        submit.value = true
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
        changeDbName(selectedName, profile)
    }

    private fun insertProfile(profile: Profile) {
        insert(profile)
    }

    fun setProfile(profile: Profile) {
        nameString.value = profile.name
        birthday = profile.birthday
        birthday?.let { birthdayString.value = Converter.longToDateStringJp(it) }
        typeString.value = profile.type
        iconJsonString = profile.icon
        genderString.value = profile.gender
    }

    fun deleteJsonString() {
        iconJsonString = null
    }

    fun setJsonString(jsonString: String) {
        iconJsonString = jsonString
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

    private fun changeDbName(oldName: String, profile: Profile) {
        viewModelScope.launch {
            profileRepository.insert(profile)
            profileRepository.deleteByName(oldName)
            diaryRepository.changeName(oldName, profile.name)
        }
    }

}