package jp.ryuk.deglog.ui.data

data class DisplayData(
    var date: String = "no data",
    var latest: String = "",
    var prev: String = "",
    var isPlusPrev: Boolean = true,
    var recent: String = "",
    var isPlusRecent: Boolean = true,
    var unit: String = ""
)