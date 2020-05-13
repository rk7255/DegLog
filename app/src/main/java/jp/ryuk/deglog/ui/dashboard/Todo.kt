package jp.ryuk.deglog.ui.dashboard

data class Todo(
    var id: Long,
    var time: String = "",
    var todo: String = "",
    var success: Boolean = false,
    var alert: Boolean = false
)