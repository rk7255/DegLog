package jp.ryuk.deglog.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class Profile (
    @PrimaryKey
    var name: String = "",
    var type: String = "未登録",
    var sex: String = "未登録",
    var birthday: Long = System.currentTimeMillis()
)