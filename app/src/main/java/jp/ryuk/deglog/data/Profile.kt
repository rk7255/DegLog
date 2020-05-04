package jp.ryuk.deglog.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import jp.ryuk.deglog.utilities.getDayOfMonth
import jp.ryuk.deglog.utilities.getMonth
import jp.ryuk.deglog.utilities.getYear
import java.time.LocalDate
import java.time.Period

@Entity(tableName = "profile_table")
data class Profile (
    @PrimaryKey var name: String = "",
    var type: String = "",
    var gender: String = "不明",
    var birthday: Long? = null,
    @ColumnInfo(name = "weight_unit") var weightUnit: String = "g",
    @ColumnInfo(name = "length_unit") var lengthUnit: String = "mm"
) {
    fun getAge(target: Long): String {
        this.birthday?.let {
            val from = LocalDate.of(it.getYear(), it.getMonth(), it.getDayOfMonth())
            val to = LocalDate.of(target.getYear(), target.getMonth(), target.getDayOfMonth())
            if (from == to) return "0日"

            val diff = Period.between(from, to)
            val age = StringBuilder()
            if (diff.years > 0) age.append("${diff.years}歳 ")
            if (diff.months > 0) age.append("${diff.months}ヶ月 ")
            if (diff.days > 0) age.append("${diff.days}日")

            return age.toString()
        }
        return "誕生日不明"
    }
}