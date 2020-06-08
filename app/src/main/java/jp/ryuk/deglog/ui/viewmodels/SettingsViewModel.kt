package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {

    val unitWeight = MutableLiveData<String>()
    val unitLength = MutableLiveData<String>()

}