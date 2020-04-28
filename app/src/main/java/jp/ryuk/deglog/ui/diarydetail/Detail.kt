package jp.ryuk.deglog.ui.diarydetail

data class Detail(
    var id: Long = 0L,
    var date: Long = System.currentTimeMillis(),
    var name: String = "",
    var weight: String? = null,
    var length: String? = null,
    var memo: String? = null,
    var age: String = "年齢不詳"
)
