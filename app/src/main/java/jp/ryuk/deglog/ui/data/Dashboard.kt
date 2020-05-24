package jp.ryuk.deglog.ui.data

data class Dashboard(
    var latest: String = "",
    var prev: String = "",
    var prevPlus: Boolean = true,
    var recent: String = "",
    var recentPlus: Boolean = true,
    var unit: String = "",
    var date: String = "記録なし"
)