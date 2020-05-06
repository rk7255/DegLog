package jp.ryuk.deglog.ui.dashboard

import java.util.*

data class Todo(
    var time: String = "",
    var todo: String = "",
    var success: Boolean = false,
    var alert: Boolean = false
)