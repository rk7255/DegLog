package jp.ryuk.deglog.ui.diarylist

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.ryuk.deglog.data.Diary
import jp.ryuk.deglog.data.DiaryDao
import jp.ryuk.deglog.data.ProfileDao
import jp.ryuk.deglog.utilities.deg

class DiaryListViewModel(
    selectedName: String,
    diaryDatabase: DiaryDao,
    profileDatabase: ProfileDao
) : ViewModel() {

    var diaries = diaryDatabase.getDiariesLive(selectedName)
    val profile = profileDatabase.getProfileLive(selectedName)

    var filteredDiaries = MediatorLiveData<List<Diary>>()

    var weightUnit = MediatorLiveData<String>()
    var lengthUnit = MediatorLiveData<String>()

    var allLoaded = MutableLiveData<Boolean>()
    var diariesLoaded = MutableLiveData<Boolean>()
    var profileLoaded = MutableLiveData<Boolean>()

    var checkedWeight = MediatorLiveData<Boolean>()
    var checkedLength = MediatorLiveData<Boolean>()
    var checkedMemo = MediatorLiveData<Boolean>()

    fun sectionLoaded() {
        if (diariesLoaded.value == true && profileLoaded.value == true) {
            weightUnit.value = profile.value?.weightUnit ?: "g"
            lengthUnit.value = profile.value?.lengthUnit ?: "mm"
            applyFilter()
            allLoaded.value = true
        }
    }

    fun applyFilter() {
        var newDiaries = diaries.value
        newDiaries = newDiaries!!.filter { it.todo == null }
        if (checkedWeight.value == true) newDiaries = newDiaries.filter { it.weight != null }
        if (checkedLength.value == true) newDiaries = newDiaries.filter { it.length != null }
        if (checkedMemo.value == true) newDiaries = newDiaries.filter { it.memo != null }
        filteredDiaries.value = newDiaries
    }
}