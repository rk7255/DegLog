package jp.ryuk.deglog.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class Profile (
    @PrimaryKey var name: String = "",
    var type: String = "",
    var gender: String = "不明",
    var birthday: Long? = null,
    @ColumnInfo(name = "weight_unit") var weightUnit: String = "g",
    @ColumnInfo(name = "length_unit") var lengthUnit: String = "mm"
)