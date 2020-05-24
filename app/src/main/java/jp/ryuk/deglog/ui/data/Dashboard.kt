package jp.ryuk.deglog.ui.data

data class Dashboard(
    var date: String = "no data",
    var latest: String = "",
    var prev: String = "",
    var isPlusPrev: Boolean = true,
    var recent: String = "",
    var isPlusRecent: Boolean = true,
    var unit: String = ""
)