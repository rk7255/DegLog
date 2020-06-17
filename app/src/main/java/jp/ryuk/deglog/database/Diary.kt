package jp.ryuk.deglog.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ryuk.deglog.utilities.DIARY_TABLE

@Entity(tableName = DIARY_TABLE)
data class Diary(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    var date: Long,
    var name: String,
    var weight: Float? = null,
    var length: Float? = null,
    @ColumnInfo(name = "free_1") var free1: Float? = null,
    @ColumnInfo(name = "free_2") var free2: Float? = null,
    var note: String? = null
)