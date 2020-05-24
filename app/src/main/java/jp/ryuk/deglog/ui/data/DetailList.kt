package jp.ryuk.deglog.ui.data

data class DetailList(
    var id: Long,
    var date: String,
    var weight: String,
    var length: String,
    var hasComment: Boolean = false
)