package jp.ryuk.deglog.ui.diarydetail

data class Detail(
    var id: Long = 0L,
    var date: Long = System.currentTimeMillis(),
    var name: String = "",
    var weight: Float? = null,
    var length: Float? = null,
    var memo: String? = null,
    var age: String = "年齢不詳"
)
