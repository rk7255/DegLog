package jp.ryuk.deglog.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ryuk.deglog.utilities.TODO_TABLE

@Entity(tableName = TODO_TABLE)
data class Todo(
    @PrimaryKey(autoGenerate = true) var id: Long = 0L,
    var date: Long,
    var name: String,
    var todo: String,
    var done: Boolean
)