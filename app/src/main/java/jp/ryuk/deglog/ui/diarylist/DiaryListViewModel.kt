package jp.ryuk.deglog.ui.diarylist

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.ProfileDao

class DiaryListViewModel(
    selectedName: String,
    diaryDatabase: DiaryDao,
    profileDatabase: ProfileDao
) : ViewModel() {

    val diaries = diaryDatabase.getDiariesLive(selectedName)
    val profile = profileDatabase.getProfileLive(selectedName)

    var weightUnit = MediatorLiveData<String>()
    var lengthUnit = MediatorLiveData<String>()
    var position = MediatorLiveData<Int>()

    var allLoaded = MutableLiveData<Boolean>()
    var diariesLoaded = MutableLiveData<Boolean>()
    var profileLoaded = MutableLiveData<Boolean>()

    fun sectionLoaded() {
        if (diariesLoaded.value == true && profileLoaded.value == true) {
            weightUnit.value = profile.value?.weightUnit ?: "g"
            lengthUnit.value = profile.value?.lengthUnit ?: "mm"
            allLoaded.value = true
        }
    }
}