package jp.ryuk.deglog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_table")
data class Diary(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var date: Long = System.currentTimeMillis(),
    var name: String = "",
    var weight: Float? = null,
    var length: Float? = null,
    var memo: String? = null
)