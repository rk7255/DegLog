package jp.ryuk.deglog.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ryuk.deglog.utilities.DIARY_TABLE
import jp.ryuk.deglog.utilities.convertUnit
import java.util.*

@Entity(tableName = DIARY_TABLE)
data class Diary(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    var date: Long,
    var name: String,
    var weight: Float? = null,
    var length: Float? = null,
    var note: String? = null
)
