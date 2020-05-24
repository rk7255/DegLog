package jp.ryuk.deglog.utilities

import android.annotation.SuppressLint
import jp.ryuk.deglog.database.Profile
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*

/**
 * Profile
 */
fun Profile.getAge(target: Long): String {
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

fun Profile.getAgeAndBirthday(): String {
    this.birthday?.let {
        val str = java.lang.StringBuilder()
        str.append(this.getAge(Calendar.getInstance().timeInMillis))
        str.append(" (${Converter.longToDateString(it)})")
        return str.toString()
    }
    return "誕生日不明"
}

/**
 * 日付
 */
@SuppressLint("SimpleDateFormat")
fun Long.getYear(): Int = SimpleDateFormat("yyyy").format(this).toInt()

@SuppressLint("SimpleDateFormat")
fun Long.getMonth(): Int = SimpleDateFormat("M").format(this).toInt()

@SuppressLint("SimpleDateFormat")
fun Long.getDayOfMonth(): Int = SimpleDateFormat("d").format(this).toInt()
