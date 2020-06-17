package jp.ryuk.deglog.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ryuk.deglog.utilities.PROFILE_TABLE

@Entity(tableName = PROFILE_TABLE)
data class Profile (
    @PrimaryKey var name: String,
    var type: String? = null,
    var gender: String? = null,
    var birthday: Long? = null,
    @ColumnInfo(name = "death_day") var deathDay: Long? = null,
    var color: Int? = null,
    var icon: String? = null
)