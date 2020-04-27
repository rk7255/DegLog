package jp.ryuk.deglog.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class Profile (
    @PrimaryKey var name: String = "",
    var type: Int = -1,
    var gender: Int = -1,
    var birthday: Long = 0L,
    @ColumnInfo(name = "weight_unit") var weightUnit: String = "g",
    @ColumnInfo(name = "length_unit") var lengthUnit: String = "mm"
)