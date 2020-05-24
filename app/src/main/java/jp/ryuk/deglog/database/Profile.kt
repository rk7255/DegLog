package jp.ryuk.deglog.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ryuk.deglog.utilities.*
import java.time.LocalDate
import java.time.Period
import java.util.*

@Entity(tableName = PROFILE_TABLE)
data class Profile (
    @PrimaryKey var name: String,
    var type: String? = null,
    var gender: String? = null,
    var birthday: Long? = null,
    @ColumnInfo(name = "weight_unit") var weightUnit: String = "g",
    @ColumnInfo(name = "length_unit") var lengthUnit: String = "mm",
    var color: Int? = null
) {
    fun getAge(target: Long): String {
        this.birthday?.let {
            val from = LocalDate.of(it.getYear(), it.getMonth(), it.getDayOfMonth())
            val to = LocalDate.of(target.getYear(), target.getMonth(), target.getDayOfMonth())
            if (from == to) return "0日"

            val diff = Period.between(from, to)
            val age = StringBuilder()
            if (diff.years <= 0 && diff.months <= 3) age.append("生後")
            if (diff.years > 0) age.append("${diff.years}歳")
            if (diff.months > 0) age.append("${diff.months}ヶ月")
            if (diff.days > 0) age.append("${diff.days}日")

            return age.toString()
        }
        return "誕生日不明"
    }

    fun getAgeAndBirthday(): String {
        this.birthday?.let {
            val str = java.lang.StringBuilder()
            str.append(this.getAge(Calendar.getInstance().timeInMillis))
            str.append(" (${convertLongToDateString(it)})")
            return str.toString()
        }
        return "誕生日不明"
    }
}