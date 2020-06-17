package jp.ryuk.deglog.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel: ViewModel() {

    val unitWeight = MutableLiveData<String>()
    val unitLength = MutableLiveData<String>()

    val isEnableDeathDay = MutableLiveData<Boolean>()

    var db1Enabled = false
    var db1Name: String? = null
    var db1Unit: String? = null

    var db2Enabled = false
    var db2Name: String? = null
    var db2Unit: String? = null

}