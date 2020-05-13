package jp.ryuk.deglog.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ryuk.deglog.utilities.convertUnit
import java.util.*

@Entity(tableName = "diary_table")
data class Diary(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var date: Long = Calendar.getInstance().timeInMillis,
    var name: String = "",
    var weight: Float? = null,
    var length: Float? = null,
    var memo: String? = null,

    var todo: String? = null,
    var success: Boolean? = null
) {
    fun convertWeightUnit(unit: String, onSuffix: Boolean): String =
        if (this.weight == null) "" else convertUnit(this.weight!!, unit, onSuffix)

    fun convertLengthUnit(unit: String, onSuffix: Boolean): String =
        if (this.length == null) "" else convertUnit(this.length!!, unit, onSuffix)
}

