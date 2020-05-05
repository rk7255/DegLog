package jp.ryuk.deglog.ui.dashboard

data class Dashboard(
    var latest: String = "0",
    var prev: String = "0",
    var prevPlus: Boolean = true,
    var recent: String = "0",
    var recentPlus: Boolean = true,
    var unit: String = "",
    var date: String = "記録なし"
)